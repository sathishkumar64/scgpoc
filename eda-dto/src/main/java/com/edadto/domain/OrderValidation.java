package com.edadto.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderValidation {

	private String orderId;
	private OrderValidationType checkType;
	private OrderValidationResult validationResult;

	public OrderValidation(String orderId2, OrderValidationType inventoryCheck, OrderValidationResult pass) {
	
		this.orderId=orderId2;
		this.checkType=inventoryCheck;
		this.validationResult=pass;
	}

	
	
	@JsonCreator
	public OrderValidation(@JsonProperty("orderId")String orderId2, @JsonProperty("checkType")String inventoryCheck, @JsonProperty("validationResult")String pass) {
		
		this.orderId=orderId2;
		this.checkType=OrderValidationType.valueOf(inventoryCheck);
		this.validationResult=OrderValidationResult.valueOf(pass);
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public OrderValidationType getCheckType() {
		return checkType;
	}

	public void setCheckType(OrderValidationType checkType) {
		this.checkType = checkType;
	}

	public OrderValidationResult getValidationResult() {
		return validationResult;
	}

	public void setValidationResult(OrderValidationResult validationResult) {
		this.validationResult = validationResult;
	}

	@Override
	public String toString() {
		return "OrderValidation [orderId=" + orderId + ", checkType=" + checkType + ", validationResult="+ validationResult + "]";
	}

	
	
}
