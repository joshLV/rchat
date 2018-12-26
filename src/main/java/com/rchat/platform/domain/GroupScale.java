package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 集团规模
 *
 * @author dzhang
 * @since 2017-04-04 17:55:39
 */
public enum GroupScale {
	/**
	 * 小型集团 0 - 100
	 */
	SMALL,
	/**
	 * 中型集团 101 - 500
	 */
	MEDIUM,
	/**
	 * 大型集团 501 - 1000
	 */
	LARGE,
	/**
	 * 超大型集团 1001!
	 */
	SUPER;

	@JsonValue
	public int value() {
		return ordinal();
	}
}
