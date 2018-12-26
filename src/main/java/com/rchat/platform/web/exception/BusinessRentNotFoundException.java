package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "rchat.exception.businessRentNotFound")
public class BusinessRentNotFoundException extends ServiceException {

	private static final long serialVersionUID = 4147498440852092944L;

	public BusinessRentNotFoundException() {
		super("续费业务不存在");
	}
}
