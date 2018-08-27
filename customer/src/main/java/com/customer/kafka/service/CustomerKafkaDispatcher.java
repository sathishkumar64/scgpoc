package com.customer.kafka.service;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.customer.domain.Customer;
import com.customer.kafka.config.KafkaCustomerProperties;

@Service
public class CustomerKafkaDispatcher {

	@Autowired
	private KafkaTemplate<String, Customer> kafkaTemplate;

	@Autowired
	private KafkaCustomerProperties kafkaProducerProperties;

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerKafkaDispatcher.class);

	public String dispatch(Customer customer) {	    
	    try {
	    	
	    	 LOGGER.info("Data = {}",customer.toString());
	    	
	        SendResult<String, Customer> sendResult = this.kafkaTemplate.send(kafkaProducerProperties.getTopic(),customer.getCustomerId(),customer).get();	            
	        RecordMetadata recordMetadata=sendResult.getRecordMetadata();	            
	        LOGGER.info("topic = {}, partition = {}, offset = {}, workUnit = {}",recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), customer);
	        return recordMetadata.topic();
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
}
