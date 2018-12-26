package com.rchat.platform.exception;

public class UserIsNotGroupAdminException extends ServiceException {

	private static final long serialVersionUID = -5024645758259242263L;

	public UserIsNotGroupAdminException() {
		super("你不是集团的管理员");
	}

}
