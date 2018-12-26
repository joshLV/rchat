package com.rchat.platform.web.format;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import com.rchat.platform.web.controller.DomainType;

public class DomainTypeFormatter implements Formatter<DomainType> {

	@Override
	public String print(DomainType object, Locale locale) {
		return object.toString().toLowerCase();
	}

	@Override
	public DomainType parse(String text, Locale locale) throws ParseException {
		DomainType[] values = DomainType.values();
		for (DomainType type : values) {
			if (type.toString().equalsIgnoreCase(text)) {
				return type;
			}
		}
		return null;
	}

}
