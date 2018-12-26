package com.rchat.platform.web.exception;

import org.springframework.security.authentication.AccountStatusException;

public class UserLogonException extends AccountStatusException {

	private static final long serialVersionUID = 1180384939196625741L;

	public UserLogonException() {
		this("用户已登录");
	}

	public UserLogonException(String msg) {
		super(msg);
	}

}
