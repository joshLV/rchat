package com.rchat.platform.common;

public interface LogContextHolderStrategy {
	void clearContext();

	LogContext getContext();

	void setContext(LogContext context);

	LogContext createEmptyContext();
}
