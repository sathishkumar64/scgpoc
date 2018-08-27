package com.edadto.domain;

public class EmailTuple {
	public Order order;
	public Customer customer;

	public EmailTuple(Order order, Customer customer) {
		this.order = order;
		this.customer = customer;
	}

	public EmailTuple setCustomer(Customer customer) {
		this.customer = customer;
		return this;
	}

	@Override
	public String toString() {
		return "EmailTuple{" + "order=" + order + ", payment=" + order.getPrice() + ", customer=" + customer + '}';
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}


	public Customer getCustomer() {
		return customer;
	}
	
	
	
}
