package com.test.awsdemo.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.test.awsdemo.dao.ImageDaoImpl;
import com.test.awsdemo.model.ImageInfo;

@Service
public class DynamoFunctionsImpl implements DynamoFunctions{

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamoFunctionsImpl.class);

	private DynamoDBMapper dynamoDBMapper;
	
	private List<ImageInfo> images;
	private Map<String, AttributeValue> lastKeyEvaluated = null;

	@Value("${db.name}")
	String databaseName;
	
    @Autowired
    private AmazonDynamoDB amazonDynamoDB;
 
    @Autowired
    ImageDaoImpl repository;
	    
	@Override
	public void setupDB() {
		LOGGER.info(" --- Database Table Creation --- ");
		images = new ArrayList<ImageInfo>();
	    dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
	        
        CreateTableRequest tableRequest = dynamoDBMapper
          .generateCreateTableRequest(ImageInfo.class);
        tableRequest.setProvisionedThroughput(
          new ProvisionedThroughput(1L, 1L));
        amazonDynamoDB.createTable(tableRequest);
	        
	}

	@Override
	public List<ImageInfo> allImages() {
		if(images == null)
			images = new ArrayList<ImageInfo>();

		LOGGER.info(" --- FETCHING IMAGES LIST --- ");
		images.clear();
		
		do {
		    ScanRequest scanRequest = new ScanRequest()
		        .withTableName(databaseName)
		        .withLimit(10)
		        .withExclusiveStartKey(lastKeyEvaluated);

		    ScanResult result = amazonDynamoDB.scan(scanRequest);
		    for (Map<String, AttributeValue> item : result.getItems()){
		    	ImageInfo  temp = new ImageInfo();
		    	temp.setUserId(item.get("userId").getS());
		    	temp.setObjectKey(item.get("originalKey").getS());
		    	temp.setThumbKey(item.get("thumbKey").getS());
		    	temp.setSourceBucket(item.get("originalBucket").getS());
		    	temp.setDestinationBucket(item.get("thumbBucket").getS());
		    	Date convertedDate;
				try {
					convertedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(item.get("createdTimeStamp").getS());
			    	temp.setCreatedTimeStamp(convertedDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					LOGGER.info(" --- UNABLE TO TRANSFORM DATE/TIME --- " + item);
				}  
				images.add(temp);
		    }
		    lastKeyEvaluated = result.getLastEvaluatedKey();
		} while (lastKeyEvaluated != null);
		
		return images;
	}

	@Override
	public void addImage(ImageInfo e) {
		LOGGER.info(" --- ADDING NEW IMAGES INFO --- ");
		images.add(e);
	}

	
}
