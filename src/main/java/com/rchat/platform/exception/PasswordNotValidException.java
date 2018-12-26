package com.rchat.platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NON_AUTHORITATIVE_INFORMATION, reason = "password.invalid")
public class PasswordNotValidException extends ServiceException {
	private static final long serialVersionUID = -8071542471259396486L;

}
