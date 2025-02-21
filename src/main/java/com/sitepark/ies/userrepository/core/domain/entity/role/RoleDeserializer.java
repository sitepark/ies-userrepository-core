package com.sitepark.ies.userrepository.core.domain.entity.role;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import java.io.IOException;

/**
 * The <code>RoleDeserializer</code> is a custom Jackson deserializer responsible for handling
 * special deserialization cases, particularly for {@link Ref} objects. It ensures proper
 * deserialization of JSON data, including the handling of {@link Ref} references within the context
 * of roles.
 *
 * @see Ref
 */
public class RoleDeserializer extends StdDeserializer<Role> {

  private static final long serialVersionUID = 1L;

  private static final RoleFactory REF_FACTORY = new RoleFactory();

  protected RoleDeserializer() {
    super(Role.class);
  }

  @Override
  public Role deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JacksonException {
    String s = p.readValueAs(String.class);
    return REF_FACTORY.create(s);
  }
}
