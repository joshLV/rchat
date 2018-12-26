package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.EXPECTATION_FAILED, reason = "rchat.exception.agentIstNotOrderRespondent")
public class AgentIsNotOrderRespondentException extends ServiceException {

	private static final long serialVersionUID = 1L;

	public AgentIsNotOrderRespondentException() {
		super("代理商不是额度订单的受理台");
	}
}
