package com.faurdCheck.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.edadto.domain.Order;
import com.edadto.domain.OrderState;
import com.edadto.domain.OrderValidation;
import com.edadto.domain.OrderValidationResult;
import com.edadto.domain.OrderValidationType;
import com.edadto.domain.OrderValue;
import com.faurdCheck.config.KafkaStreamProperties;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class FraudService  {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FraudService.class);
	
	@Autowired
	private KafkaStreamProperties kafkaStreamProperties;
	
	@Value("${orderValidationTopic}")
	private String orderValidationTopic;
	
	private static final int FRAUD_LIMIT = 2000;
	private static final long MIN = 60 * 1000L;
	
	
	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
	public StreamsConfig kStreamsConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaStreamProperties.getGroup());
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaStreamProperties.getBootstrap());
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());	
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, new JsonSerde<>(Order.class).getClass());

		return new StreamsConfig(props);
	}
	
	
	 @Bean
	 public KStream<String, Order> kStream(StreamsBuilder kStreamBuilder) {
		 
		 LOGGER.info("Farud Service Starting.............................");
		 
		 KStream<String, Order> orders = kStreamBuilder.stream(kafkaStreamProperties.getTopic(),Consumed.with(Serdes.String(),new JsonSerde<>(Order.class)))
				 .filter((id, order) -> OrderState.CREATED.toString().toLowerCase().equals(order.getOrderState().toLowerCase()));  
		
		 KTable<Windowed<String>, OrderValue> aggregate = orders.groupBy((id, order) -> order.getCustomerId(), Serialized.with(Serdes.String(), new JsonSerde<>(Order.class)))
				 	.windowedBy(SessionWindows.with(60 * MIN))
		 			.aggregate(OrderValue::new,  
		 					   (custId, order, total) -> new OrderValue(order,(total.getValue() + (order.getQuantity() * order.getPrice()))), 
		 					   (aggKey, aggOne, aggTwo) -> simpleMerge(aggOne, aggTwo), 
		 					   Materialized.with(null, new JsonSerde<>(OrderValue.class)));
			        		 
		  KStream<String, OrderValue> ordersWithTotals = aggregate.toStream((windowedKey, orderValue) -> windowedKey.key())
			        .filter((k, v) -> v != null)//When elements are evicted from a session window they create delete events. Filter these out.
			        .selectKey((id, orderValue) -> orderValue.getOrder().getOrderId());
		 
		 
		  KStream<String, OrderValue>[] forks = ordersWithTotals.branch((id, orderValue) -> orderValue.getValue() >= FRAUD_LIMIT,(id, orderValue) -> orderValue.getValue() < FRAUD_LIMIT);

		    forks[0].mapValues(orderValue -> new OrderValidation(orderValue.getOrder().getOrderId(), OrderValidationType.FRAUD_CHECK, OrderValidationResult.FAIL))
		    .peek((key, value) -> LOGGER.info("Order Fraud Check Status:::::::::key=" + key + ", value=" + value))
		        .to(orderValidationTopic, Produced.with(Serdes.String(), new JsonSerde<>(OrderValidation.class)));

		    forks[1].mapValues(orderValue -> new OrderValidation(orderValue.getOrder().getOrderId(), OrderValidationType.FRAUD_CHECK, OrderValidationResult.PASS))
		    .peek((key, value) -> LOGGER.info("Order Fraud Check Status:::::::::key=" + key + ", value=" + value))
		        .to(orderValidationTopic, Produced.with(Serdes.String(), new JsonSerde<>(OrderValidation.class)));
	 		 
		 orders.peek((key, value) -> LOGGER.info("Order Fraud Check::::::::::key=" + key + ", value=" + value));
		 
		 return orders;
	 }
	
	
	

	
	 private OrderValue simpleMerge(OrderValue a, OrderValue b) {
		 return new OrderValue(b.getOrder(), (a == null ? 0D : a.getValue()) + b.getValue());
	 }
}
