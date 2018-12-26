package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "rchat.exception.talkbackGroupNotFound")
public class TalkbackGroupNotFoundException extends ServiceException {

	private static final long serialVersionUID = 1636938490592206088L;

	public TalkbackGroupNotFoundException() {
		super("对讲群组不存在");
	}
}
