package com.rchat.platform.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.AttributeConverter;

import org.apache.commons.lang3.StringUtils;

public class CallOutPermissionConverter implements AttributeConverter<List<CallOutPermission>, String> {

	@Override
	public String convertToDatabaseColumn(List<CallOutPermission> attribute) {
		if (attribute == null)
			return "";

		return StringUtils.join(attribute, ",");
	}

	@Override
	public List<CallOutPermission> convertToEntityAttribute(String dbData) {
		if (StringUtils.isBlank(dbData)) {
			return Collections.emptyList();
		}

		String[] _permissions = dbData.split(",");
		List<CallOutPermission> permissions = new ArrayList<>();

		for (String p : _permissions) {
			permissions.add(CallOutPermission.valueOf(p));
		}
		return permissions;
	}
}
