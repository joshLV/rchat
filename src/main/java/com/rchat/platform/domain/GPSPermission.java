package com.rchat.platform.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 对讲用户GPS定位权限
 * 
 * @author dzhang
 *
 */
@Embeddable
public class GPSPermission implements Serializable {
	private static final long serialVersionUID = -8895185053089782642L;
	/**
	 * 定位使能
	 */
	@Column(name = "gps_enabled")
	private boolean enabled;
	/**
	 * 定位时间间隔
	 */
	@Column(name = "gps_interval")
	private int interval;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	@Override
	public String toString() {
		return String.format("GPSPermission [enabled=%s, interval=%s]", enabled, interval);
	}
}
