package com.edadto.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Order implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String orderId;
	
	private String orderState;
	
	private String productId;
	
	private String customerId;
	
	private int quantity;
	
	private double price;
	
	@JsonCreator
	public Order(@JsonProperty("orderId")String orderId, @JsonProperty("orderState")String orderState) {
		super();
		this.orderId = orderId;
		this.orderState = orderState;		
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Order [orderId=" + orderId + ", orderState=" + orderState + ", productId=" + productId + ", customerId="
				+ customerId + ", quantity=" + quantity + ", price=" + price + "]";
	}
	



}
