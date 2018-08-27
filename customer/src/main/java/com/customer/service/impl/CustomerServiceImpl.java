package com.customer.service.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.customer.dao.CustomerRepository;
import com.customer.domain.Customer;
import com.customer.kafka.service.CustomerKafkaDispatcher;
import com.customer.service.CustomerService;


@Service("customerService")
@Transactional
public class CustomerServiceImpl implements CustomerService {
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	CustomerKafkaDispatcher customerKafkaDispatcher;

	@Override
	public Customer findByCode(String customerId) {
		Customer customer =customerRepository.findByCustomerId(customerId);
		return customer;
	}

	@Override
	public Customer saveCustomer(Customer product) {	
		Customer customerObj =customerRepository.save(product);
		return customerObj;
	}

	@Override
	public List<Customer> findAllCustomer() {
		List<Customer> listCustomer =customerRepository.findAll();
		return listCustomer;
	}

	@Override
	public String feedToTopic() {
		List<Customer> listProduct =customerRepository.findAll();		
		for (Iterator<Customer> iterator = listProduct.iterator(); iterator.hasNext();) {
			Customer customer = iterator.next();
			customerKafkaDispatcher.dispatch(customer);			
		}		
		return "Process is Done";
	}

}
