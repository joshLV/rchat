package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 群组类型
 * 
 * @author dzhang
 *
 */
public enum TalkbackGroupType {
	/**
	 * 默认群组
	 */
	DEFAULT,
	/**
	 * 部门群组
	 */
	DEPARTMENT,
	/**
	 * 预定义群组
	 */
	PREDEFINED;

	@JsonValue
	public int value() {
		return ordinal();
	}

}
