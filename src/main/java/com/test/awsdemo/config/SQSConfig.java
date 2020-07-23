package com.test.awsdemo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

@Configuration
@EnableSqs
public class SQSConfig {
	
/*	 
   @Value("${cloud.aws.credentials.accessKey}")
   String accessKey;
   
   @Value("${cloud.aws.credentials.secretKey}")
   String accessSecret;	
*/	   
	
	@Value("${cloud.aws.region.static}")
	String region;
	
    private static final Logger LOGGER = LoggerFactory.getLogger(SQSConfig.class);
	
    @Bean
    public QueueMessagingTemplate queueMessagingTemplate() {
    	LOGGER.info("--- SQS Configuration Completed---");
        return new QueueMessagingTemplate(amazonSQSClient());
    }
    
    
    @Bean(name ="awsClient")
    @Primary
    public AmazonSQSAsync amazonSQSClient() {
        return AmazonSQSAsyncClientBuilder.standard()
                                        .withRegion(Regions.fromName(region))
                                        .withCredentials(new DefaultAWSCredentialsProviderChain())
                                        .build();
    }
    
}
