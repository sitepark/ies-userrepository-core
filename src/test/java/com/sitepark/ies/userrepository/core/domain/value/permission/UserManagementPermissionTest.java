package com.sitepark.ies.userrepository.core.domain.value.permission;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;

class UserManagementPermissionTest {

  @Test
  void testSerialize() {

    JsonMapper mapper =
        JsonMapper.builder()
            .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .changeDefaultPropertyInclusion(
                inclusion -> inclusion.withValueInclusion(JsonInclude.Include.NON_EMPTY))
            .build();

    UserManagementPermission permission =
        UserManagementPermission.builder()
            .userGrant(UserGrant.builder().read(true).build())
            .build();

    String json = mapper.writeValueAsString(permission);

    String expected =
        """
        {"type":"USER_MANAGEMENT","userGrant":{"read":true}}\
        """;

    assertEquals(expected, json, "unexpected json");
  }
}
