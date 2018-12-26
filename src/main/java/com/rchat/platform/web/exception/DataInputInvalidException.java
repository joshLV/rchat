package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "rchat.exception.dataInputInvalid")
public class DataInputInvalidException extends ServiceException {

	private static final long serialVersionUID = -6140435152331220979L;

	public DataInputInvalidException() {
		super("无效数据");
	}

	public DataInputInvalidException(String message) {
		super(message);
	}
}
