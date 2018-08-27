/*package com.order.util;

import java.io.File;

import org.codehaus.jackson.map.ObjectMapper;

import com.order.domain.Order;

public class JsonSimpleWriteExample {
	public static void main(String[] args) {

		
		Order order = new Order();
		order.setOrderId("eda001");
		order.setCustomerId(64L);
		order.setOrderState("created");
		order.setPrice(10);
		order.setQuantity(1);
		order.setProductId	("001");
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(new File("c://temp/order.json"), order);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
*/