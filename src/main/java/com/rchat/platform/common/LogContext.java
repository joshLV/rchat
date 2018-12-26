package com.rchat.platform.common;

import com.rchat.platform.domain.Log;

public interface LogContext {
	Log getLog();

	void setLog(Log log);
}
