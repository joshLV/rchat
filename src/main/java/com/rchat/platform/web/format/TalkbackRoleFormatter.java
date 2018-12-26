package com.rchat.platform.web.format;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import com.rchat.platform.domain.TalkbackRole;

public class TalkbackRoleFormatter implements Formatter<TalkbackRole> {

	@Override
	public String print(TalkbackRole object, Locale locale) {
		return String.valueOf(object.ordinal());
	}

	@Override
	public TalkbackRole parse(String text, Locale locale) throws ParseException {
		TalkbackRole[] roles = TalkbackRole.values();
		for (TalkbackRole role : roles) {
			if (role.ordinal() == Integer.parseInt(text)) {
				return role;
			}
		}

		return null;
	}
}
