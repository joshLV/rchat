package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "rchat.exception.groupSegmentNotEmpty")
public class GroupSegmentNotEmptyException extends ServiceException {

	private static final long serialVersionUID = 1L;

	public GroupSegmentNotEmptyException() {
		super("集团号段不为空");
	}
}
