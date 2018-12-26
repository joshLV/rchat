package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 对讲用户呼出权限
 * 
 * @author dzhang
 *
 */
public enum CallOutPermission {
	/**
	 * 单向呼出
	 */
	UNIDIRECTIONAL,
	/**
	 * 临时组呼出
	 */
	PROVISIONAL,
	/**
	 * 预定义组呼出
	 */
	PREDEFINED;

	@JsonValue
	public int value() {
		return ordinal();
	}
}
