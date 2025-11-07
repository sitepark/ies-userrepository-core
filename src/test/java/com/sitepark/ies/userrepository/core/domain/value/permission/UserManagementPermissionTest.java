package com.sitepark.ies.userrepository.core.domain.value.permission;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.Test;

class UserManagementPermissionTest {

  @Test
  void testSerialize() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    UserManagementPermission permission =
        UserManagementPermission.builder()
            .userGrant(UserGrant.builder().read(true).build())
            .build();

    String json = mapper.writeValueAsString(permission);

    String expected = """
        {"type":"USER_MANAGEMENT","userGrant":{"read":true}}""";

    assertEquals(expected, json, "unexpected json");
  }

  @Test
  void testDeserialize() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    SimpleModule module = new SimpleModule();
    mapper.registerModule(module);

    String json = """
        {"type":"USER_MANAGEMENT","userGrant":{"read":true}}""";

    UserManagementPermission permission = mapper.readValue(json, UserManagementPermission.class);

    UserManagementPermission expected =
        UserManagementPermission.builder()
            .userGrant(UserGrant.builder().read(true).build())
            .build();

    assertEquals(expected, permission, "unexpected permission");
  }
}
