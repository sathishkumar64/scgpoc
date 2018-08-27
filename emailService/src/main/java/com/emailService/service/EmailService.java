package com.emailService.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.edadto.domain.Customer;
import com.edadto.domain.EmailTuple;
import com.edadto.domain.Order;
import com.edadto.domain.OrderState;
import com.emailService.config.KafkaStreamProperties;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class EmailService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
	
	
	@Autowired
	private KafkaStreamProperties kafkaStreamProperties;
	
	public static final long MIN = 60 * 1000L;	
	
	@Autowired
	private Emailer emailer;
	

	@Value("${customerTopic}")
	private String customerTopic;

	
	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
	public StreamsConfig kStreamsConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG,  kafkaStreamProperties.getGroup());
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaStreamProperties.getBootstrap());
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());	
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, new JsonSerde<>(Order.class).getClass());
	    props.put(JsonDeserializer.DEFAULT_KEY_TYPE, String.class);
		return new StreamsConfig(props);
	}
	
	
	
	 @Bean
	 public KStream<String, Order> kStream(StreamsBuilder kStreamBuilder) {		
		
		 LOGGER.info("Email Service Starting .............................");
		
		 KStream<String, Order> orders = kStreamBuilder.stream(kafkaStreamProperties.getTopic(),Consumed.with(Serdes.String(),new JsonSerde<>(Order.class)));
		 
		 final GlobalKTable<String, Customer> customers = kStreamBuilder.globalTable(customerTopic,Consumed.with(Serdes.String(), new JsonSerde<>(Customer.class)));  
		 
		  orders.filter((id, order) -> OrderState.SHIPPED.toString().toLowerCase().equals(order.getOrderState().toLowerCase()))
		 .selectKey((key, value) -> value.getCustomerId())
		 .join(customers, (key,customer) -> key, (order, customer) ->new EmailTuple(order,customer))
		 .peek((key, emailTuple)-> LOGGER.info("Customer::::::::::key=" + key + ", value=" + emailTuple));
		 //.peek((key, emailTuple)-> emailer.sendEmail(emailTuple));
		
		 return orders;
	 }
	

}
