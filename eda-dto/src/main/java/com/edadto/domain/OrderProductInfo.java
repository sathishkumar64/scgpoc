package com.edadto.domain;

public class OrderProductInfo {

	private Order order;
	private Product product;
	
	public OrderProductInfo(Order order, Product product) {
		super();
		this.order = order;
		this.product = product;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
	
	
}
