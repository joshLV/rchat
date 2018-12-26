package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 对讲用户角色
 * 
 * @author dzhang
 * 
 */
public enum TalkbackRole {
	/**
	 * 调度员
	 */
	DISPATCHER,
	/**
	 * 对讲用户
	 */
	TALKBACK_USER;

	@JsonValue
	public int value() {
		return ordinal();
	}
}
