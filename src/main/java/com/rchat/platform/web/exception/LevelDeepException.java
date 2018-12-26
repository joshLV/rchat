package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "rchat.exception.levelDeep")
public class LevelDeepException extends ServiceException {

	private static final long serialVersionUID = 3470574847909996624L;

	public LevelDeepException() {
		super("结构深度太深");
	}
}
