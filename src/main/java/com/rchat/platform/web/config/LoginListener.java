package com.rchat.platform.web.config;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;

public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {
	public LoginListener() {
	}

	@Override
	public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
	}
}