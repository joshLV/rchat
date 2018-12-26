package com.rchat.platform.exception;

public class UnkownAgentTypeException extends ServiceException {

	private static final long serialVersionUID = -2530512192669570910L;

	public UnkownAgentTypeException() {
		super("未知代理商类型");
	}
}
