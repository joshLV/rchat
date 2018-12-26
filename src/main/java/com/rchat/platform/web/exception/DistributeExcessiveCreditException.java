package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.EXPECTATION_FAILED, reason = "rchat.exception.distributeExcessiveCredit")
public class DistributeExcessiveCreditException extends ServiceException {
	private static final long serialVersionUID = 1L;

	public DistributeExcessiveCreditException() {
		super("下发额度超过订单额度");
	}
}
