package com.rchat.platform.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.AttributeConverter;

import org.apache.commons.lang3.StringUtils;

public class CallInPermissionConverter implements AttributeConverter<List<CallInPermission>, String> {

	@Override
	public String convertToDatabaseColumn(List<CallInPermission> attribute) {
		if (attribute == null) {
			return "";
		}

		return StringUtils.join(attribute, ",");
	}

	@Override
	public List<CallInPermission> convertToEntityAttribute(String dbData) {
		if (StringUtils.isBlank(dbData)) {
			return Collections.emptyList();
		}

		String[] _permissions = dbData.split(",");
		List<CallInPermission> permissions = new ArrayList<>();

		for (String p : _permissions) {
			permissions.add(CallInPermission.valueOf(p));
		}

		return permissions;
	}
}
