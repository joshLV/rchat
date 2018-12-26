package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "rchat.exception.creditOrderNotFound")
public class CreditOrderNotFoundException extends ServiceException {
	private static final long serialVersionUID = 1L;

	public CreditOrderNotFoundException() {
		super("额度订单不存在");
	}
}
