package com.rchat.platform.web.format;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import com.rchat.platform.domain.AgentType;

public class AgentTypeFormatter implements Formatter<AgentType> {

	@Override
	public String print(AgentType object, Locale locale) {
		return object.name().toLowerCase();
	}

	@Override
	public AgentType parse(String text, Locale locale) throws ParseException {
		AgentType[] values = AgentType.values();
		for (AgentType type : values) {
			if (type.name().equalsIgnoreCase(text)) {
				return type;
			}
		}
		return null;
	}

}
