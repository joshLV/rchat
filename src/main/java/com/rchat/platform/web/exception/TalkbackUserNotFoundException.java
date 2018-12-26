package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "rchat.exception.talbackUserNotFoundException")
public class TalkbackUserNotFoundException extends ServiceException {

	private static final long serialVersionUID = -8469328771725589715L;

	public TalkbackUserNotFoundException() {
		super("对讲账户不存在");
	}

}
