package com.test.awsdemo;


import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSAsync;

@Component
public class ApplicationStarter {
	
	// To Stop SQS Queue Listener Gracefully
	@Bean
	public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSqs) {
	    SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
	    factory.setWaitTimeOut(5);
	    return factory;
	}
}
