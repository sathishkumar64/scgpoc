package com.edadto.domain;

public class Customer {

	private String idCustomer;

	private String customerName;

	private String email;

	private Long mobile;

	private boolean status;

	public String getIdCustomer() {
		return idCustomer;
	}

	public void setIdCustomer(String idCustomer) {
		this.idCustomer = idCustomer;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Customer [idCustomer=" + idCustomer + ", customerName=" + customerName + ", email=" + email
				+ ", mobile=" + mobile + ", status=" + status + "]";
	}
	
	
}
