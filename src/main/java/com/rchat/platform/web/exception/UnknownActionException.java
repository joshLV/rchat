package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "rchat.exception.unknownAction")
public class UnknownActionException extends ServiceException {
	private static final long serialVersionUID = -228864263502613346L;

	public UnknownActionException() {
		super("未知操作");
	}
}
