package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "rchat.exception.talbackUsersNull")
public class TalbackUsersNullException extends ServiceException {

	private static final long serialVersionUID = -3810309918480158025L;

	public TalbackUsersNullException() {
		super("对讲账号列表不能为空");
	}
}
