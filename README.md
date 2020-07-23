# aws-cloud-practice

http://vaibhavdes.github.io/aws-cloud-practice

# Lambda Function Invocation
  Attach Trigger as S3 Bucket where Original Image will be uploaded
  
# Attach IAM Role & Policies to Lambda & EC2 
  1) Lambda : S3 Read and Write / SQS Send Message /  CloudWatch Log Group
  2) EC2 : S3 Read and Write / SQS Read & Delete Message / DynamoDB Read/Write

# EC2 Security Group
  1) TCP : 8080 : Anywhere (SPRING PROJECT PORT)
  2) HTTP : 80 (Default) : AnyWhere (If hosting any Static WebPage)
  3) SSH : (Default)
  
#  Spring Project Deployment Steps

1) Connect to EC2 via SSH or in Browser Way
2) Execute Following Commands

    # change privilege
    sudo su
    # install os updates (YUM is a software package management utility)
    yum update -y
    # install apache server
    yum install httpd -y
    # check if any jdk installed
    java -version
    # install java version 8
    yum install java-1.8.0 -y
    # download the JAR of Spring Project from S3 (Replace the Bucket Name, Key/File Name, and AZ-Region where Bucket is Present are passed as parameter)
    # alternatively you can use "wget" if S3 Bucket is public
    aws s3 cp s3://bucketnamedemo/awsdemo-0.0.1-SNAPSHOT.jar / --region=us-east-1
    # run app
    java -jar awsdemo-0.0.1-SNAPSHOT.jar
    
    # to stop app press "Ctrl + C)
    
    # in case you are detached from application and still its using port and running (Replace Port Number If you are using other)
    lsof -i:8080
    # kill the application process running
    kill -9 $(lsof -t -i:8080)
