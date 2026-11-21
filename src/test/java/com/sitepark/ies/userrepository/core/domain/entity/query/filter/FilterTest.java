package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class FilterTest {

  @Test
  void testId() {
    Id filter = Filter.id("123");
    assertEquals("123", filter.getId(), "unexpected id");
  }

  @Test
  void testIdList() {
    IdList filter = Filter.idList("123");
    assertEquals(Arrays.asList("123"), filter.getIdList(), "unexpected idList");
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
    assertEquals(Arrays.asList(anchor), filter.getAnchorList(), "unexpected anchorList");
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

    JsonMapper objectMapper =
        JsonMapper.builder().disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY).build();
    String json = objectMapper.writeValueAsString(filter);

    assertEquals(
        """
        {\"or":[{"idList":["6"]},{"anchor":"abc"},{"and":[{"lastName":"test"},{"login":"test"}]}]}\
        """,
        json,
        "unexpected json-data");
  }
}
