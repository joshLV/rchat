package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "rchat.exception.numberInsufficiency")
public class NumberInsufficiencyException extends ServiceException {

	private static final long serialVersionUID = -8352242296687684828L;

	public NumberInsufficiencyException() {
		super("号码资源不足");
	}
}
