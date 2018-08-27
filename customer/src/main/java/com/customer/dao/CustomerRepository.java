package com.customer.dao;


import java.util.List;

import org.springframework.data.repository.Repository;

import com.customer.domain.Customer;

public interface CustomerRepository extends Repository<Customer,Long> {

	Customer findByCustomerId(String customerId);	
	 
	 List<Customer> findAll();
	 
	 Customer save(Customer customer);
}
