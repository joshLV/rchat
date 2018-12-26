package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "rchat.exception.businessNotFound")
public class BusinessNotFoundException extends ServiceException {
	private static final long serialVersionUID = 8318914160440716957L;

	public BusinessNotFoundException() {
		super("业务不存在");
	}
}
