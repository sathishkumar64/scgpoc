package com.order.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.edadto.domain.Order;
import com.order.service.OrderCommandService;

@RestController   
@RequestMapping(path="/api")
public class OrderController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private OrderCommandService orderCommandService;
	
	@PostMapping(path="/order")
	public @ResponseBody String createOrder (@RequestBody Order order) {
		LOGGER.info("Creating Order...."+order);
		orderCommandService.createOrder(order);
		return "saved";
	}
}
