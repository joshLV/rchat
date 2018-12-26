package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "rchat.exception.logNotFound")
public class LogNotFoundException extends ServiceException {

	private static final long serialVersionUID = -8874409884248141975L;

	public LogNotFoundException() {
		super("日志不存在");
	}

}
