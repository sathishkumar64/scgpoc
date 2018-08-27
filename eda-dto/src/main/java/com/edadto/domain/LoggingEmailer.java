package com.edadto.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingEmailer {

	private static final Logger log = LoggerFactory.getLogger(LoggingEmailer.class);
	
	public void sendEmail(EmailTuple details) {	   
	      log.warn("Sending an email to: \nCustomer:%s\nOrder:%s\nPayment%s", details.customer,details.order);
	    }
}
