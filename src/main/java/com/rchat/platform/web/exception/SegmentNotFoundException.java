package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "rchat.exception.segmentNotFound")
public class SegmentNotFoundException extends ServiceException {

	private static final long serialVersionUID = -8916045697218965474L;

	public SegmentNotFoundException() {
		super("号段不存在");
	}
}
