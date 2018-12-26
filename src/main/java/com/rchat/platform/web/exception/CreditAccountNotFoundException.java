package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "rchat.exception.creditAccountNotFound")
public class CreditAccountNotFoundException extends ServiceException {

	private static final long serialVersionUID = -4763808788821582034L;

	public CreditAccountNotFoundException() {
		super("额度账户不存在");
	}

}
