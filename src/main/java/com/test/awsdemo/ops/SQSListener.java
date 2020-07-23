package com.test.awsdemo.ops;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.test.awsdemo.dao.ImageDao;
import com.test.awsdemo.model.ImageInfo;
import com.test.awsdemo.service.DynamoFunctions;



@Service
public class SQSListener {
	
	//@Autowired
	//ImageInfoRepository db;

	@Autowired
	ImageDao db;
	
	@Autowired
	DynamoFunctions dynamo;
	
    private static final Logger LOGGER = LoggerFactory.getLogger(SQSListener.class);

    @SqsListener(value = "${queue.name}", deletionPolicy = SqsMessageDeletionPolicy.NEVER)
    
    public void listen(String message, Acknowledgment acknowledgment, @Headers Map<String, String> headers)
            throws ExecutionException, InterruptedException {

        LOGGER.info(" --- Recieved Message From SQS : message = {} ", message.toString());

        JSONObject info = new JSONObject(message);
	  
      	//ImageInfo ii = new Gson().fromJson(message, ImageInfo.class);
	  
	   	//LOGGER.info("--- INFO : " + ii.getDestinationBucket());
	   	//LOGGER.info("--- INFO : " + ii.getObjectKey());
	   	//LOGGER.info("--- INFO : " + ii.getSourceBucket());
       
       try {
	       if(!info.isEmpty()) {
	    	   ImageInfo imageInfo = new ImageInfo();
	    	   imageInfo.setSourceBucket(info.getString("sourceBucket"));
	    	   imageInfo.setObjectKey(info.getString("objectKey"));
	    	   imageInfo.setDestinationBucket(info.getString("destinationBucket"));
	    	   imageInfo.setThumbKey(info.getString("objectKey"));	    
	    	   
	    	   //dynamo.addImage(imageInfo);

	    	   System.out.println(db.createEntry(imageInfo));	  	   	    	   
	       }
       } catch (AmazonDynamoDBException db) {
           LOGGER.info("--- OCCURED EXCEPTION MSG : " +  db.getErrorMessage());
           LOGGER.info("--- OCCURED EXCEPTION CODE : " + db.getErrorCode());
           LOGGER.info("--- OCCURED EXCEPTION REQ ID : " +  db.getRequestId());
       }
       
        /*
          process the message
          if processed successfully, delete from QUEUE
        */
        acknowledgment.acknowledge().get();
    }
}
