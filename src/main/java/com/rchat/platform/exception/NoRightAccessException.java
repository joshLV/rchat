package com.rchat.platform.exception;

/**
 * 无权限访问
 *
 * @author dzhang
 * @since 2017-02-27 21:12:54
 */
public class NoRightAccessException extends ServiceException {

	private static final long serialVersionUID = 1L;

	public NoRightAccessException() {
		super("无访问权限");
	}

}
