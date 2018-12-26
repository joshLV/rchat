package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "rchat.exception.noAvailableNumber")
public class NoAvailableNumberException extends ServiceException {

	private static final long serialVersionUID = 9116999993607335306L;

	public NoAvailableNumberException() {
		super("没有空用的号码了");
	}
}
