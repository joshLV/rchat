package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.EXPECTATION_FAILED, reason = "rchat.exception.idNotMatch")
public class IdNotMatchException extends ServiceException {
	private static final long serialVersionUID = 6313581689627481053L;

	public IdNotMatchException() {
		super("实体Id不匹配");
	}
}
