package com.test.awsdemo.dao;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDeleteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.test.awsdemo.model.ImageInfo;

@Repository
//@EnableScan
public class ImageDaoImpl implements ImageDao{

	private static final Logger LOGGER = LoggerFactory.getLogger(ImageDaoImpl.class);

	@Autowired
    private DynamoDBMapper db;

	@Override
	public ImageInfo createEntry(ImageInfo ii) {
		 LOGGER.info("--- NEW IMAGE ENTRY ---");
		 db.save(ii);
		 return ii;
	}

	@Override
	public ImageInfo updateEntry(ImageInfo ii) {
		 LOGGER.info("--- IMAGE ENTRY UPDATED ---");
		// Note expectedAttributeValueMap is introduced in later version of DynamoDB SDK
		//Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
        //expectedAttributeValueMap.put("userId", new ExpectedAttributeValue(new AttributeValue().withS(ii.getUserId())));
        //DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression().withExpected(expectedAttributeValueMap);
		try {
			db.save(ii, buildDynamoDBSaveExpression(ii));
		} catch (ConditionalCheckFailedException exception) {
			LOGGER.error("Data Not Found - ", exception.getMessage());
		}
        return ii;
        
	}

	@Override
	public ImageInfo fetchEntry(String userId) {
		LOGGER.info("--- GET REQUEST FOR IMAGE INFO ---");
        return db.load(ImageInfo.class, userId);
	}

	@Override
	public void deleteEntry(String userId) {
		LOGGER.info("--- DELETE REQUEST FOR IMAGE INFO ---");
		// OR PASS OBJECT WITH JUST USERID SET
		 Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
		 expectedAttributeValueMap.put("userId", new ExpectedAttributeValue(new AttributeValue().withS(userId)));
	     DynamoDBDeleteExpression deleteExpression = new DynamoDBDeleteExpression().withExpected(expectedAttributeValueMap);
	     ImageInfo ii = new ImageInfo(userId);
	     db.delete(ii, deleteExpression);
		
	}
	
	public DynamoDBSaveExpression buildDynamoDBSaveExpression(ImageInfo ii) {
		LOGGER.info("--- SEARCH IMAGE INFO BY ID  ---");
		DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
		Map<String, ExpectedAttributeValue> expected = new HashMap<>();
		expected.put("userId", new ExpectedAttributeValue(new AttributeValue(ii.getUserId()))
				.withComparisonOperator(ComparisonOperator.EQ));
		saveExpression.setExpected(expected);
		return saveExpression;
	}

}
