package com.rchat.platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "rchat.exception.userIsNotAgentAdmin")
public class UserIsNotDepartmentAdminException extends ServiceException {
	private static final long serialVersionUID = 7917506746661838216L;

	public UserIsNotDepartmentAdminException() {
		super("你不是部门或者公司管理员");
	}
}
