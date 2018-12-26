package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "rchat.exception.segmentNotMatch")
public class SegmentNotMathException extends ServiceException {

	private static final long serialVersionUID = -5378267642582070807L;

	public SegmentNotMathException() {
		super("号段不匹配");
	}

}
