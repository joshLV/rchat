package com.rchat.platform.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ActiveMQClientDemo {
	@JmsListener(destination = "rchat.topic")
	public void agentChanged(String message) {
		System.err.println(message);
	}
}
