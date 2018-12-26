package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "rchat.exception.passwordInvalid")
public class PasswordInvalidException extends ServiceException {

	private static final long serialVersionUID = -105309814981355903L;

	public PasswordInvalidException() {
		super("密码错误");
	}

}
