package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "rchat.exception.agentNotBelongToCurrentAgent")
public class AgentNotBelongToCurrentAgent extends ServiceException {

	private static final long serialVersionUID = -5438413159519761226L;

	public AgentNotBelongToCurrentAgent() {
		super("代理商不属于当前代理商");
	}
}
