# aws-cloud-practice

# 1. Lambda Function to Process Image File

a) Install Necessary Packages
  npm install
  
b) Generate Build to Deploy to Lambda
  npm build
  
c) Upload as Zip 
    OR
   Upload to S3 Bucket and Provide Object URL in (Function Code - Action Section)
   
# 2. Add Following Environment Variables for Lambda Function
  DEST_BUCKET = S3 Bucket Name to Store Processed Image
  
  QUEUE_URL = SQS Queue URL to Send the Image & Bucket Information
  
  QUALITY = Configure Image Quality Between 1 - 100 (Low to High)
  
  
# 3. Add Event Trigger 
  Configure Event Trigger on S3 Bucket on which Image will be Uploaded
  (* Do not have same bucket to upload original and processed image, it will form a Loop)

# 4. Configure Handler (Function to be called on Event) in Basic Settings
  Handler - src/index.handler
  
  Timeout - 10 sec
  
  Runtime - Node.js 12.x

# 5. Configure Asynchronous invocationInfo
  Configure Retry Attemps in case of failure and Max Age of Event
  
# 6. Configure Monitoring
  To Store Logs for Each Execution in CloudWatch LogGroups
