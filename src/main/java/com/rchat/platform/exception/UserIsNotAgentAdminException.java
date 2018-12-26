package com.rchat.platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "rchat.exception.userIsNotAgentAdmin")
public class UserIsNotAgentAdminException extends ServiceException {

	private static final long serialVersionUID = 1879919176658185861L;

	public UserIsNotAgentAdminException() {
		super("用户不是代理商管理员");
	}
}
