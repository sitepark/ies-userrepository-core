package com.sitepark.ies.userrepository.core.domain.entity.role;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.sitepark.ies.userrepository.core.domain.entity.Role;

/**
 * The <code>RoleDeserializer</code> is a custom Jackson deserializer responsible for
 * handling special deserialization cases, particularly for {@link Ref} objects.
 * It ensures proper deserialization of JSON data, including the handling of  {@link Ref}
 * references within the context of roles.
 *
 * @see Ref
 */
public class RoleDeserializer extends StdDeserializer<Role> {

	private static final long serialVersionUID = 1L;

	private static final RefFactory REF_FACTORY = new RefFactory();

	protected RoleDeserializer() {
		super(Role.class);
	}

	@Override
	public Role deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		String s = p.readValueAs(String.class);
		if (REF_FACTORY.accept(s)) {
			return REF_FACTORY.create(s);
		}
		return Role.ofName(s);
	}

}
