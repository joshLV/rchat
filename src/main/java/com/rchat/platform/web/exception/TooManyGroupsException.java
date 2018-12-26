package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, reason = "rchat.exception.tooManyGroups")
public class TooManyGroupsException extends ServiceException {

	private static final long serialVersionUID = -7000615327586705748L;

	public TooManyGroupsException() {
		super("过多集团");
	}

}
