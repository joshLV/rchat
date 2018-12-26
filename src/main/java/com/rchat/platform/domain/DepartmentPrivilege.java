package com.rchat.platform.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 部门权限
 * 
 * @author dzhang
 *
 */
@Embeddable
public class DepartmentPrivilege implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 跨级呼叫方式
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "over_level_call_type")
	@JsonProperty
	private OverLevelCallType overLevelCallType;
	/**
	 * 跨组呼叫是否允许
	 */
	@Column(name = "over_group_enabled")
	private boolean overGroupEnabled;
	/**
	 * GPS定位是否开启
	 */
	@Column(name = "gps_enabled")
	private boolean gpsEnabled;

	public OverLevelCallType getOverLevelCallType() {
		return overLevelCallType;
	}

	public void setOverLevelCallType(OverLevelCallType overLevelCallType) {
		this.overLevelCallType = overLevelCallType;
	}

	public boolean isOverGroupEnabled() {
		return overGroupEnabled;
	}

	public void setOverGroupEnabled(boolean overGroupEnabled) {
		this.overGroupEnabled = overGroupEnabled;
	}

	public boolean isGpsEnabled() {
		return gpsEnabled;
	}

	public void setGpsEnabled(boolean gpsEnabled) {
		this.gpsEnabled = gpsEnabled;
	}

	/**
	 * 跨级呼叫方式
	 * 
	 * @author dzhang
	 *
	 */
	public static enum OverLevelCallType {
		/**
		 * 禁止
		 */
		DISABLED,
		/**
		 * 单呼
		 */
		UNIDIRECTIONAL,
		/**
		 * 临时组呼
		 */
		TEMP_GROUP;

		@JsonValue
		public int value() {
			return ordinal();
		}
	}

}
