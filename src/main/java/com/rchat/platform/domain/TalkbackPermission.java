package com.rchat.platform.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 * 对讲权限
 * 
 * @author dzhang
 *
 */
@Embeddable
public class TalkbackPermission implements Serializable {

	private static final long serialVersionUID = -5557799163441083790L;

	// @Enumerated(EnumType.ORDINAL)
	@Convert(converter = CallInPermissionConverter.class)
	private List<CallInPermission> callInPermissions;
	// @Enumerated(EnumType.ORDINAL)
	@Convert(converter = CallOutPermissionConverter.class)
	private List<CallOutPermission> callOutPermissions;
	@Embedded
	private GPSPermission gpsPermission;

	public List<CallInPermission> getCallInPermissions() {
		return callInPermissions;
	}

	public void setCallInPermissions(List<CallInPermission> callInPermissions) {
		this.callInPermissions = callInPermissions;
	}

	public List<CallOutPermission> getCallOutPermissions() {
		return callOutPermissions;
	}

	public void setCallOutPermissions(List<CallOutPermission> callOutPermissions) {
		this.callOutPermissions = callOutPermissions;
	}

	public GPSPermission getGpsPermission() {
		return gpsPermission;
	}

	public void setGpsPermission(GPSPermission gpsPermission) {
		this.gpsPermission = gpsPermission;
	}
}
