package com.sitepark.ies.userrepository.core.usecase.query.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.ies.userrepository.core.domain.entity.databind.DatabindModule;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class FilterTest {

  @Test
  void testId() {
    Id filter = Filter.id("123");
    assertEquals("123", filter.getId(), "unexpected id");
  }

  @Test
  void testIdList() {
    IdList filter = Filter.idList("123");
    assertEquals(List.of("123"), filter.getIdList(), "unexpected idList");
  }

  @Test
  void testAnchor() {
    com.sitepark.ies.userrepository.core.domain.entity.Anchor anchor =
        com.sitepark.ies.userrepository.core.domain.entity.Anchor.ofString("abc");
    Anchor filter = Filter.anchor(anchor);
    assertEquals(anchor, filter.getAnchor(), "unexpected anchorList");
  }

  @Test
  void testAnchorList() {
    com.sitepark.ies.userrepository.core.domain.entity.Anchor anchor =
        com.sitepark.ies.userrepository.core.domain.entity.Anchor.ofString("abc");
    AnchorList filter = Filter.anchorList(anchor);
    assertEquals(List.of(anchor), filter.getAnchorList(), "unexpected anchorList");
  }

  @Test
  void testFirstName() {
    FirstName filter = Filter.firstName("Peter");
    assertEquals("Peter", filter.getFirstName(), "unexpected firstName");
  }

  @Test
  void testLastName() {
    LastName filter = Filter.lastName("Pan");
    assertEquals("Pan", filter.getLastName(), "unexpected lastName");
  }

  @Test
  void getLogin() {
    Login filter = Filter.login("panpan");
    assertEquals("panpan", filter.getLogin(), "unexpected login");
  }

  @Test
  void getEmail() {
    Email filter = Filter.email("panpan@neverland.com");
    assertEquals("panpan@neverland.com", filter.getEmail(), "unexpected email");
  }

  @Test
  void getRoleId() {
    RoleId filter = Filter.roleId("123");
    assertEquals("123", filter.getRoleId(), "unexpected roleId");
  }

  @Test
  void getRoleIdList() {
    RoleIdList filter = Filter.roleIdList("123");
    assertEquals(List.of("123"), filter.getRoleIdList(), "unexpected roleIdList");
  }

  @Test
  void getPrivilegeId() {
    PrivilegeId filter = Filter.privilegeId("123");
    assertEquals("123", filter.getPrivilegeId(), "unexpected privilegeId");
  }

  @Test
  void getPrivilegeIdList() {
    PrivilegeIdList filter = Filter.privilegeIdList("123");
    assertEquals(List.of("123"), filter.getPrivilegedList(), "unexpected privilegeIdList");
  }

  @Test
  void testOr() {
    Filter a = mock();
    Filter b = mock();
    Or filter = Filter.or(a, b);
    assertEquals(Arrays.asList(a, b), filter.getOr(), "unexpected or");
  }

  @Test
  void testAnd() {
    Filter a = mock();
    Filter b = mock();
    And filter = Filter.and(a, b);
    assertEquals(Arrays.asList(a, b), filter.getAnd(), "unexpected and");
  }

  @Test
  void testNot() {
    Filter a = mock();
    Not filter = Filter.not(a);
    assertEquals(a, filter.getNot(), "unexpected not");
  }

  @Test
  void testSerialize() throws Exception {

    Filter filter =
        Filter.or(
            Filter.idList("6"),
            Filter.anchor(
                com.sitepark.ies.userrepository.core.domain.entity.Anchor.ofString("abc")),
            Filter.and(Filter.lastName("test"), Filter.login("test")));

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(filter);

    assertEquals(
        """
        {"or":[{"idList":["6"]},{"anchor":"abc"},{"and":[{"lastName":"test"},{"login":"test"}]}]}\
        """,
        json,
        "unexpected json-data");
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
  void testDeserialize() throws Exception {

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new DatabindModule());

    String json =
        """
        {
            "or":[
                {"idList":["6"]},
                {"anchor":"abc"},
                {
                    "and":[
                        {"login":"login"},
                        {"firstName":"firstName"},
                        {
                            "not":{"lastName":"lastName"}
                        }
                    ]
                }
            ]
        }
        """;

    Filter filter = objectMapper.readValue(json, Filter.class);

    Filter expected =
        Filter.or(
            Filter.idList("6"),
            Filter.anchor(
                com.sitepark.ies.userrepository.core.domain.entity.Anchor.ofString("abc")),
            Filter.and(
                Filter.login("login"),
                Filter.firstName("firstName"),
                Filter.not(Filter.lastName("lastName"))));

    assertEquals(expected, filter, "unexpected filter");
  }
}
