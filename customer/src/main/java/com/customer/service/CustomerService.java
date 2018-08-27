package com.customer.service;

import java.util.List;

import com.customer.domain.Customer;



public interface CustomerService {

	Customer findByCode(String id);	
	
	Customer saveCustomer(Customer customer);

	List<Customer> findAllCustomer();
	
	String feedToTopic();
}
