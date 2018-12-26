package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "rchat.exception.segmentExists")
public class SegmentExistsException extends ServiceException {

	private static final long serialVersionUID = -2478433439632039440L;

	public SegmentExistsException() {
		super("号段已经存在");
	}

}
