package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 群组类型
 * 
 * @author dzhang
 *
 */
public enum EquipmentType {
	/**
	 * 对讲设备
	 */
	INTERCOM,
	/**
	 * 视频设备
	 */
	VIDEO,
	/**
	 * IP话机
	 */
	PHONE,
	/**
	 * 广播设备
	 */
	BROADCAST,
	/**
	 * 监控设备
	 */
	MONITOR,
	/**
	 * 其他设备
	 */
	OTHERS;

	@JsonValue
	public int value() {
		return ordinal();
	}

}
