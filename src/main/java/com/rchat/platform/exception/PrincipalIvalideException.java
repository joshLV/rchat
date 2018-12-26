package com.rchat.platform.exception;

public class PrincipalIvalideException extends ServiceException {
	private static final long serialVersionUID = 5506371634912504201L;

	public PrincipalIvalideException() {
		super("认证实体非法");
	}

}
