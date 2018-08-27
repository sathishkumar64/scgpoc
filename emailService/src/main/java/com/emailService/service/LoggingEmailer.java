package com.emailService.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.edadto.domain.EmailTuple;

@Service("emailer")
public class LoggingEmailer implements Emailer {
	
  private static final Logger log = LoggerFactory.getLogger(LoggingEmailer.class);

    @Override
    public void sendEmail(EmailTuple details) {
      //In a real implementation we would do something a little more useful
      log.warn("Sending an email to: \nCustomer:%s\nOrder:%s\nPayment%s", details);
    }
}
