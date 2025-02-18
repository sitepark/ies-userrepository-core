package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.ies.userrepository.core.domain.entity.databind.DatabindModule;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

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

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(filter);

    assertEquals(
        """
        {\"or":[{"idList":["6"]},{"anchor":"abc"},{"and":[{"lastName":"test"},{"login":"test"}]}]}\
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

    assertInstanceOf(Or.class, filter);

    Or or = (Or) filter;

    assertInstanceOf(IdList.class, or.getOr().get(0));
    assertInstanceOf(Anchor.class, or.getOr().get(1));
    assertEquals("abc", ((Anchor) (or.getOr().get(1))).getAnchor().getName(), "wront root");
    assertInstanceOf(And.class, or.getOr().get(2));

    And and = (And) or.getOr().get(2);

    assertInstanceOf(Login.class, and.getAnd().get(0));
    assertEquals("login", ((Login) and.getAnd().get(0)).getLogin(), "wront login");
    assertInstanceOf(FirstName.class, and.getAnd().get(1));
    assertEquals("firstName", ((FirstName) and.getAnd().get(1)).getFirstName(), "wront first name");
    assertInstanceOf(Not.class, and.getAnd().get(2));

    Not not = (Not) and.getAnd().get(2);

    assertInstanceOf(LastName.class, not.getNot());
    assertEquals("lastName", ((LastName) not.getNot()).getLastName(), "wront last name (in not)");
  }
}
