package com.rchat.platform.common;

public class LogContextHolder {
	private static LogContextHolderStrategy strategy = new ThreadLocalLogContextHolderStrategy();

	public static final LogContext initializeLogContext() {
		LogContext ctx = createEmptyContext();
		setLogContext(ctx);
		return ctx;
	}

	public static final LogContext getLogContext() {
		return strategy.getContext();
	}

	public static final void setLogContext(LogContext context) {
		strategy.setContext(context);
	}

	public static final LogContextHolderStrategy getContextHolderStrategy() {
		return strategy;
	}

	public static final LogContext createEmptyContext() {
		return strategy.createEmptyContext();
	}

	public static void clearContext() {
		strategy.clearContext();
	}
}
