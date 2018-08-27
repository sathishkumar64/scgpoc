package com.order.kafka.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.edadto.domain.Order;
import com.order.kafka.config.KafkaProducerProperties;

@Service
public class OrderKafkaDispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderKafkaDispatcher.class);

	@Autowired
	private KafkaTemplate<String, Order> kafkaTemplate;

	@Autowired
	private KafkaProducerProperties kafkaProducerProperties;

	public boolean dispatch(Order order) {
		ProducerRecord<String, Order> record = new ProducerRecord<>(kafkaProducerProperties.getTopic(),	order.getOrderId(), order);
		try {
			SendResult<String, Order> sendResult = this.kafkaTemplate.send(record).get();
			RecordMetadata recordMetadata = sendResult.getRecordMetadata();
			LOGGER.info("topic = {}, partition = {}, offset = {}, workUnit = {}", recordMetadata.topic(),
					recordMetadata.partition(), recordMetadata.offset(), order);
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
