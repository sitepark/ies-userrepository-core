package com.sitepark.ies.userrepository.core.domain.entity;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sitepark.ies.userrepository.core.domain.entity.role.RoleDeserializer;

@JsonDeserialize(using = RoleDeserializer.class)
public class Role {

	@JsonValue
	private final String name;

	protected Role(String name) {
		assert name != null : "name is null";
		assert !name.isBlank() : "name is blank";
		this.name = name;
	}

	public static Role ofName(String name) {
		return new Role(name);
	}

	public String getName() {
		return this.name;
	}

	@Override
	public final int hashCode() {
		return this.name != null ? this.name.hashCode() : 0;
	}

	@Override
	public final boolean equals(Object o) {

		if (!(o instanceof Role)) {
			return false;
		}

		Role other = (Role)o;
		return Objects.equals(this.name, other.name);
	}

	@Override
	public String toString() {
		return this.name;
	}
}
