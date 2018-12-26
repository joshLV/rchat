package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "rchat.exception.talkbackUserDiasbled")
public class TalkbackUserDisabledException extends ServiceException {

	private static final long serialVersionUID = -638253175866419805L;

	public TalkbackUserDisabledException() {
		super("对讲账号已经停用");
	}
}
