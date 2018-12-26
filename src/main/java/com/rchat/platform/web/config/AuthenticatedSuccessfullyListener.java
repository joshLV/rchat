package com.rchat.platform.web.config;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

import com.rchat.platform.common.RchatUtils;

public class AuthenticatedSuccessfullyListener implements ApplicationListener<AuthenticationSuccessEvent> {

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		RchatUtils.currentSession().ifPresent(session -> RchatUtils.login(RchatUtils.currentUser(), session));
	}
}
