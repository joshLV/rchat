package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "rchat.exception.groupNotFound")
public class GroupNotFoundException extends ServiceException {

	private static final long serialVersionUID = 4833237654004908502L;

	public GroupNotFoundException() {
		super("集团不存在");
	}
}
