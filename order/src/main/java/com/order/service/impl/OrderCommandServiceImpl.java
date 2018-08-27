package com.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edadto.domain.Order;
import com.order.kafka.service.OrderKafkaDispatcher;
import com.order.service.OrderCommandService;

@Service("orderCommandService")
@Transactional
public class OrderCommandServiceImpl implements OrderCommandService{

	@Autowired
	private OrderKafkaDispatcher orderKafkaDispatcher;
	
	
	@Override
	public void createOrder(Order order) {
		orderKafkaDispatcher.dispatch(order);		
	}

	
	/*@Autowired
	OrderCommandRepository orderCommandRepository;
	
	
	@Override
	public Order saveOrder(Order order) {
		Order orderObj=orderCommandRepository.save(order);
		return orderObj;
	}

	@Override
	public Order updateOrder(Order order) {
		Order orderObj=orderCommandRepository.update(order);
		return orderObj;
	}*/
}
