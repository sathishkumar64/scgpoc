package com.product.kafka.service;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.product.domain.Product;
import com.product.kafka.config.KafkaProducerProperties;

@Service
public class ProductKafkaDispatcher {

	@Autowired
	private KafkaTemplate<String, Product> kafkaTemplate;

	@Autowired
	private KafkaProducerProperties kafkaProducerProperties;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductKafkaDispatcher.class);

	public String dispatch(Product product) {	    
	        try {
	        	LOGGER.info("Data = {}",product.toString());	        	
	            SendResult<String, Product> sendResult = this.kafkaTemplate.send(kafkaProducerProperties.getTopic(),product.getProductCode(),product).get();	            
	            RecordMetadata recordMetadata=sendResult.getRecordMetadata();	            
	            LOGGER.info("topic = {}, partition = {}, offset = {}, workUnit = {}",recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), product);
	            return recordMetadata.topic();
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }
}
