package com.test.awsdemo.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

@Service
public class S3FileOperationImpl implements S3FileOperation {

	private Logger LOGGER = LoggerFactory.getLogger(S3FileOperationImpl.class);

	@Autowired
	AmazonS3 s3client;
	
	@Autowired
	S3Bucket bucket;
	  
	@Override
	public void uploadFile(String bucketName, String fileName,  MultipartFile file) {
        LOGGER.info(" --- Uploading a new file to S3 Bucket --- ");

		if(!s3client.doesBucketExistV2(bucketName))
			bucket.createBucket(bucketName);
			
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            s3client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(),metadata));
        } catch (IOException e) {
        	LOGGER.error("IOException: " + e.getMessage());
		} catch (AmazonServiceException a) {
			  LOGGER.info("Error Message:    " + a.getMessage());
		      LOGGER.info("HTTP Status Code: " + a.getStatusCode());
		      LOGGER.info("AWS Error Code:   " + a.getErrorCode());
		      LOGGER.info("Error Type:       " + a.getErrorType());
		      LOGGER.info("Request ID:       " + a.getRequestId());
		}
	}

	@Override
	public void deleteFile(String bucketName, String key) {
		LOGGER.info(" --- Deleting file from bucket --- ");
	    s3client.deleteObject(bucketName, key);
	}

	@Override
	public byte[] getFile(String key, String bucketName) {
	try {	
		 if(s3client.doesBucketExistV2(bucketName)) {		
			  LOGGER.info(" --- Downloading an file from bucket --- ");
			  S3Object obj = s3client.getObject(new GetObjectRequest(bucketName, key));
		      S3ObjectInputStream stream = obj.getObjectContent();
	      
	      LOGGER.info("Content-Type: "  + obj.getObjectMetadata().getContentType());
	
	        try {
	            byte[] content = IOUtils.toByteArray(stream);
	            obj.close();
	            return content;
	        } catch (IOException e) {
	        	LOGGER.error("IOException: " + e.getMessage());
	        } catch (AmazonServiceException a) {
	        	LOGGER.info("Error Message:    " + a.getMessage());
	        	LOGGER.info("HTTP Status Code: " + a.getStatusCode());
	        	LOGGER.info("AWS Error Code:   " + a.getErrorCode());
	        	LOGGER.info("Error Type:       " + a.getErrorType());
	        	LOGGER.info("Request ID:       " + a.getRequestId());
	        }
		 }
	} catch (AmazonServiceException a) {
    	LOGGER.info("Error Message:    " + a.getMessage());
    	LOGGER.info("HTTP Status Code: " + a.getStatusCode());
    	LOGGER.info("AWS Error Code:   " + a.getErrorCode());
    	LOGGER.info("Error Type:       " + a.getErrorType());
    	LOGGER.info("Request ID:       " + a.getRequestId());
    }
      return null;
	}
	
	public void copyFile(String srcBucket, String srcFileName,String destBucket,String destFileName) {
		LOGGER.info(" --- Copying a File to Another S3 Bucket --- ");

		 if(s3client.doesBucketExistV2(srcBucket)) {		
			 if(s3client.doesBucketExistV2(destBucket))
					bucket.createBucket(destBucket);
			 s3client.copyObject(srcBucket, srcFileName, destBucket, destFileName);
		 }
	}

}
