package com.rchat.platform.common;

import com.rchat.platform.domain.Log;

public class LogContextImpl implements LogContext {
	private Log log;

	public LogContextImpl() {
	}

	public LogContextImpl(Log log) {
		setLog(log);
	}

	@Override
	public Log getLog() {
		return log;
	}

	@Override
	public void setLog(Log log) {
		this.log = log;
	}

	@Override
	public String toString() {
		return String.format("LogContextImpl [log=%s]", log);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((log == null) ? 0 : log.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LogContextImpl))
			return false;
		LogContextImpl other = (LogContextImpl) obj;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
		return true;
	}

}
