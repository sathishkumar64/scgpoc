package com.inventoryCheck.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
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
import com.edadto.domain.OrderProductInfo;
import com.edadto.domain.OrderState;
import com.edadto.domain.OrderValidation;
import com.edadto.domain.Product;
import com.inventoryCheck.config.KafkaStreamProperties;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class InventoryService  {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);
	
	public static final String RESERVED_STOCK_STORE_NAME = "store-of-reserved-stock";

	
	@Autowired
	private KafkaStreamProperties kafkaStreamProperties;
	
	@Value("${productTopic}")
	private String productTopic;
	
	@Value("${orderValidationTopic}")
	private String orderValidationTopic;

	
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
		
		 LOGGER.info("InventoryService Check Starting.............................");
	
		 GlobalKTable<String, Product> productGlobalTable = kStreamBuilder.globalTable(productTopic,Consumed.with(Serdes.String(),new JsonSerde<>(Product.class)));   
		 
		 KStream<String, Order> orderPurchases = kStreamBuilder.stream(kafkaStreamProperties.getTopic(),Consumed.with(Serdes.String(),new JsonSerde<>(Order.class)));  
		 
		 StoreBuilder<?> reservedStock = Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(RESERVED_STOCK_STORE_NAME),
				 Serdes.String(),Serdes.Long()).withLoggingEnabled(new HashMap<>());
		 
		 kStreamBuilder.addStateStore(reservedStock);	
		 
		 orderPurchases.selectKey((id, order) -> order.getProductId())						
		 				.filter((id, order) -> OrderState.CREATED.toString().toLowerCase().equals(order.getOrderState().toLowerCase()))	
		 				.join(productGlobalTable, (key,order) -> key,  (orderPurchase, prodcutInfo) ->new OrderProductInfo(orderPurchase,prodcutInfo))
		 				.transform(InventoryValidator::new, RESERVED_STOCK_STORE_NAME)
		 				.to(orderValidationTopic, Produced.with(Serdes.String(),new JsonSerde<>(OrderValidation.class)));	
		 
		 	
		 
		// ReadOnlyKeyValueStore<String, Long> keyValueStore =kStreamBuilder.store("CountsKeyValueStore", QueryableStoreTypes.keyValueStore());
		 
		 
		 
		// orderPurchases.peek((key, value) ->  LOGGER.info("Order::::::::::key=" + key + ", value=" + value));
	
		 return orderPurchases;
	 }
	
	
	
	
}
