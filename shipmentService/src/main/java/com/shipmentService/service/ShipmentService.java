package com.shipmentService.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;

import com.edadto.domain.Order;
import com.edadto.domain.OrderState;
import com.edadto.domain.OrderValidation;
import com.shipmentService.config.KafkaStreamProperties;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class ShipmentService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShipmentService.class);
	

	public static final String ORDER_PAYMENT_STORE_NAME = "store-of-order-payment-stock";
	
	@Autowired
	private KafkaStreamProperties kafkaStreamProperties;
	
	
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
		 LOGGER.info("Shipment Service Starting.............................");		
		 
		 StoreBuilder<?> reservedStock = Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(ORDER_PAYMENT_STORE_NAME),
				 Serdes.String(),Serdes.String()).withLoggingEnabled(new HashMap<>());
		 kStreamBuilder.addStateStore(reservedStock);	
		 KStream<String, Order> orders = kStreamBuilder.stream(kafkaStreamProperties.getTopic(),Consumed.with(Serdes.String(),new JsonSerde<>(Order.class)));
		 
		 orders.filter((id, order) -> OrderState.VALIDATED.toString().toLowerCase().equals(order.getOrderState().toLowerCase()))
				 .transform(PaymentValidator::new, ORDER_PAYMENT_STORE_NAME)
				 .to(kafkaStreamProperties.getTopic(), Produced.with(Serdes.String(),new JsonSerde<>(Order.class)));	
		 return orders;
	}
}
