package com.shipmentService.service;

import java.util.concurrent.TimeUnit;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edadto.domain.Order;
import com.edadto.domain.OrderState;

public class PaymentValidator implements Transformer<String, Order, KeyValue<String, Order>>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentValidator.class);

	public static final String ORDER_PAYMENT_STORE_NAME = "store-of-order-payment-stock";
	private KeyValueStore<String, String> orderPaymentStore;
	
	@Override
	public void init(ProcessorContext context) {
		KeyValueStore<String, String> stateStore = (KeyValueStore<String, String>) context.getStateStore(ORDER_PAYMENT_STORE_NAME);
		orderPaymentStore = stateStore;
		
	}

	@Override
	public KeyValue<String, Order> transform(String key, Order value) {
		
		String orderCurrentState = value.getOrderState();
		
		String orderState = orderPaymentStore.get(value.getOrderId());	
		
		 LOGGER.info("order State from Store...............Order Id "+value.getOrderId() +" State "+orderState +" From Order.............."+orderCurrentState);
		
		if (orderState == null) {
			orderState = OrderState.VALIDATED.toString();
		}
		
		try {
			
			TimeUnit.SECONDS.sleep(5);
			/**Let assume payment is done, based on payment order state become shipped*/
			value.setOrderState(OrderState.SHIPPED.toString());	
			orderPaymentStore.put(value.getOrderId(),value.getOrderState());
			
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
	    LOGGER.info("Order ID::::::::::::::::::" + value.getOrderId() + " State::::::::::" + value.getOrderState());
		return KeyValue.pair(key, value);
	}

	@Override
	public KeyValue<String, Order> punctuate(long timestamp) {
		return null;
	}

	@Override
	public void close() {
	}

}
