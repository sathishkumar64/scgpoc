package com.edadto.domain;

public class OrderValue {

	private Order order;
	
	private double value;
	
	public OrderValue() {
		super();		
	}

	public OrderValue(Order order, double value) {
		super();
		this.order = order;
		this.value = value;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "OrderValue [order=" + order + ", value=" + value + "]";
	}

}
