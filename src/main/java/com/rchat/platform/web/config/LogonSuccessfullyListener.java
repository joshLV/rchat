package com.rchat.platform.web.config;

import org.springframework.context.ApplicationListener;
import org.springframework.security.web.session.HttpSessionCreatedEvent;

import com.rchat.platform.common.RchatUtils;

public class LogonSuccessfullyListener implements ApplicationListener<HttpSessionCreatedEvent> {

	@Override
	public void onApplicationEvent(HttpSessionCreatedEvent event) {
		RchatUtils.currentSession(event.getSession());
	}
}
