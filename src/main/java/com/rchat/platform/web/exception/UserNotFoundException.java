package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "rchat.exception.userNotFound")
public class UserNotFoundException extends ServiceException {
	private static final long serialVersionUID = -3724754368203979036L;

	public UserNotFoundException() {
		super("rchat.exception.userNotFound");
	}
}
