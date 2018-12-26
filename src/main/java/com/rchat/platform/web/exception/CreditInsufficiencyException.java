package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "rchat.exception.creditInsufficiency")
public class CreditInsufficiencyException extends ServiceException {

	private static final long serialVersionUID = 6288134322233227385L;

	public CreditInsufficiencyException() {
		super("额度不足");
	}

}
