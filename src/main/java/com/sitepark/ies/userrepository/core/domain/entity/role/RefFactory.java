package com.sitepark.ies.userrepository.core.domain.entity.role;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;

public class RefFactory implements RoleFactory {

	private static final long serialVersionUID = 1L;

	private static final Pattern PATTERN_ID = Pattern.compile("^REF\\(([0-9]+)\\)$");

	private static final Pattern PATTERN_ANCHOR = Pattern.compile("^REF\\((" + Anchor.VALID_CHARS_REGEX + ")\\)$");

	@Override
	public boolean accept(String role) {
		return
				PATTERN_ID.matcher(role).matches() ||
				PATTERN_ANCHOR.matcher(role).matches();
	}

	@Override
	public Ref create(String role) {

		Matcher idMatcher = PATTERN_ID.matcher(role);
		if (idMatcher.matches()) {
			long id = Long.valueOf(idMatcher.group(1), 10);
			return Ref.ofId(id);
		}

		Matcher anchorMatcher = PATTERN_ANCHOR.matcher(role);
		if (anchorMatcher.matches()) {
			String anchor = anchorMatcher.group(1);
			return Ref.ofAnchor(anchor);
		}

		throw new IllegalArgumentException("Invalid role: " + role);
	}
}
