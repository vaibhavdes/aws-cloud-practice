# aws-cloud-practice

Policies :

1) SQSBasicAccessPolicy
	Resource : SQS
	Action :
		Read: 
			GetQueueAttributes
			GetQueueUrl,
			ReceiveMessage
		Write: 
			ChangeMessageVisibility 
			CreateQueue
			DeleteMessage
			SendMessage
			SendMessageBatch
	Access:
		QueueName
		Region

Note: SendMessage - Need for Lambda Function
	  Other are needed for EC2 Spring Project
	  
2) S3BasicAccessPolicy
	Resource : S3
	Action : 
		Read:
			GetObject
		Write:
			PutObject
	Access:
		BucketName
		Region

Note: Need for Lambda & EC2 Project to Upload and Download

3) DynamoDBAccessPolicy
	Resource : DynamoDB
	Action :
		List :
			ListTables
		Read :
			BatchGetItem
			GetItem
			GetRecords
			GetShardIterator
			ListStreams
			Query
			Scan
		Write :
			CreateTable
			DeleteItem
			PutItem
			UpdateItem
			UpdateTable
	Access:
		TableName
		Region
Note : Needed for EC2 Project for DB Operations


4) LambdaExecutionRole
	Resource : CloudWatch Logs
	Action :
		Write :
			CreateLogStream
			PutLogEvents
	Access :
		LogGroupName
		Region

Note : Needed for Lambda Function for Logs
