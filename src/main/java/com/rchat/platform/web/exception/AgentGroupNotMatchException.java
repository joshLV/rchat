package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "rchat.exception.agentGroupNotMatch")
public class AgentGroupNotMatchException extends ServiceException {

	private static final long serialVersionUID = 2650105941478591899L;

	public AgentGroupNotMatchException() {
		super("代理商和集团信息不匹配");
	}
}
