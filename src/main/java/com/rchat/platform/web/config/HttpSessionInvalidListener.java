package com.rchat.platform.web.config;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.User;
import com.rchat.platform.service.UserService;

public class HttpSessionInvalidListener implements ApplicationListener<HttpSessionDestroyedEvent> {
	@Autowired
	private UserService userService;

	public HttpSessionInvalidListener() {
		System.out.println("HttpSessionInvalidListener.HttpSessionInvalidListener()");
	}

	@Override
	public void onApplicationEvent(HttpSessionDestroyedEvent event) {
		HttpSession session = event.getSession();
		Object value = session.getAttribute(RchatUtils.SECURITY_USER);
		Optional.ofNullable(value).ifPresent(user -> getUserService(session).logout((User) user));
	}

	private UserService getUserService(HttpSession session) {
		if (userService != null) {
			return userService;
		}
		WebApplicationContext context = WebApplicationContextUtils
				.findWebApplicationContext(session.getServletContext());

		userService = context.getBean(UserService.class);
		return userService;
	}
}
