package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "rchat.exception.TalbackNumberNotEmpty")
public class TalkbackNumberNotEmptyException extends ServiceException {

	private static final long serialVersionUID = 1L;

	public TalkbackNumberNotEmptyException() {
		super("对讲账号不为空");
	}

}
