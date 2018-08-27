package com.emailService.service;

import com.edadto.domain.EmailTuple;

public interface Emailer {
	public void sendEmail(EmailTuple details);
}
