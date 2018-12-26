package com.rchat.platform.domain;

/**
 * 可记录日志的资源
 * 
 * @author dzhang
 *
 */
public interface Loggable {
	LogDetail toLogDetail();
}
