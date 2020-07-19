const aws = require('aws-sdk');
const jimp = require('jimp');
const s3 = new aws.S3();
const sqs = new aws.SQS();

const destBucket = process.env.DEST_BUCKET;
const queueURL = process.env.QUEUE_URL;
const quality = parseInt(process.env.QUALITY);

exports.handler = function main(event, context) {
  // Fail on mising data
  if (!destBucket || !quality) {
    context.fail('Error: Environment variable DEST_BUCKET or QUALITY missing');
    return;
  }
  if (event.Records === null) {
    context.fail('Error: Event has no records.');
    return;
  }
  if (!queueURL) {
    context.fail('Error: Queue URL is missing from Environment Variable.');
    return;
  }
  
  // Make a task for each record
  let tasks = [];
  for (let i = 0; i < event.Records.length; i++) {
    tasks.push(conversionPromise(event.Records[i], destBucket));
  }

  Promise.all(tasks)
    .then(() => { context.succeed(); })
    .catch((err) => { context.fail(err); });
};

function conversionPromise(record, destBucket) {
  return new Promise((resolve, reject) => {
    // The source bucket and source key are part of the event data
    const srcBucket = record.s3.bucket.name;
    const srcKey = decodeURIComponent(record.s3.object.key.replace(/\+/g, " "));

    // Modify destKey if an alternate copy location is preferred
    const destKey = srcKey;
    const conversion = 'compressing (quality ' + quality + '): ' + srcBucket + ':' + srcKey + ' to ' + destBucket + ':' + destKey;

    console.log('Attempting: ' + conversion);

    get(srcBucket, srcKey)
      .then(original => compress(original))
      .then(modified => put(destBucket, destKey, modified))
      .then(message => sqsmessage(destBucket, destKey, srcBucket, message))
      .then(() => {
        console.log('Success: ' + conversion);
        return resolve('Success: ' + conversion);
      })
      .catch(error => {
        console.error(error);
        return reject(error);
      });
  });
}

function get(srcBucket, srcKey) {
  return new Promise((resolve, reject) => {
    s3.getObject({
      Bucket: srcBucket,
      Key: srcKey
    }, (err, data) => {
      if (err) {
        console.error('Error getting object: ' + srcBucket + ':' + srcKey);
        return reject(err);
      } else {
        resolve(data.Body);
      }
    });
  });
}

function put(destBucket, destKey, data) {
  return new Promise((resolve, reject) => {
    s3.putObject({
      Bucket: destBucket,
      Key: destKey,
      Body: data
    }, (err, data) => {
      if (err) {
        console.error('Error putting object: ' + destBucket + ':' + destKey);
        return reject(err);
      } else {
        resolve(data);
      }
    });
  });
}

async function compress(inBuffer) {
  const image = await jimp.read(inBuffer);
  image.quality(quality);
  return image.getBufferAsync(jimp.MIME_JPEG);
}

function sqsmessage(destBucket, destKey, srcBucket, message) {
// Send File Information to SQS Queue
  var someData = {
    sourceBucket: srcBucket,
    destinationBucket: destBucket,
    objectKey: destKey,
  };
  var params = {
    DelaySeconds: 10,
    MessageAttributes: {
      "DestBucket": {
        DataType: "String",
        StringValue: destBucket
      },
      "SrcBucket": {
        DataType: "String",
        StringValue: srcBucket
      },
      "Key": {
        DataType: "String",
        StringValue: destKey
      },
      "Time": {
        DataType: "String",
        StringValue: new Date().toString()
      }
    },
    MessageBody: JSON.stringify(someData),
    // MessageDeduplicationId: "TheWhistler",  // Required for FIFO queues
    // MessageGroupId: "Group1",  // Required for FIFO queues
    QueueUrl: queueURL
  };

  return new Promise((resolve, reject) => {
    sqs.sendMessage(params, function (err, data) {
      if (err) {
        console.log("Error Sending Message to SQS : ", err);
        reject(err);
      } else {
        console.log("Message Sent Succesfully to SQS : ", data.MessageId);
        resolve(data.MessageId);
      }
    });
  });
}
