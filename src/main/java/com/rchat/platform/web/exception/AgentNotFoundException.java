package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

/**
 * 代理商找不到
 *
 * @author dzhang
 * @since 2017-04-04 20:22:52
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "rchat.exception.agentNotFound")
public class AgentNotFoundException extends ServiceException {
	private static final long serialVersionUID = 8977631246099666735L;

	public AgentNotFoundException() {
		super("代理商不存在");
	}
}
