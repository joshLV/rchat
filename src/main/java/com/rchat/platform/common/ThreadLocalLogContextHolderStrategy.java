package com.rchat.platform.common;

import com.rchat.platform.domain.Log;
import com.rchat.platform.domain.User;

class ThreadLocalLogContextHolderStrategy implements LogContextHolderStrategy {

	private static final ThreadLocal<LogContext> contextHolder = new ThreadLocal<>();

	@Override
	public void clearContext() {
		contextHolder.remove();
	}

	@Override
	public LogContext getContext() {
		LogContext ctx = contextHolder.get();
		return ctx;
	}

	@Override
	public void setContext(LogContext context) {
		contextHolder.set(context);
	}

	@Override
	public LogContext createEmptyContext() {
		User user = RchatUtils.currentUser();
		Log log = new Log(user);
		return new LogContextImpl(log);
	}

}
