package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NON_AUTHORITATIVE_INFORMATION, reason = "rchat.exception.usernameNotFound")
public class UsernameNotFoundException extends ServiceException {

	private static final long serialVersionUID = -7586019347821589267L;

	public UsernameNotFoundException() {
		super("用户名不存在");
	}

}
