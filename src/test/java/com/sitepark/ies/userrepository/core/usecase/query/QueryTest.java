package com.sitepark.ies.userrepository.core.usecase.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.sitepark.ies.userrepository.core.usecase.query.filter.Filter;
import com.sitepark.ies.userrepository.core.usecase.query.limit.Limit;
import com.sitepark.ies.userrepository.core.usecase.query.sort.SortCriteria;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class QueryTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    EqualsVerifier.forClass(Query.class).verify();
  }

  @Test
  void testSetFilter() {
    Filter filter = mock();
    Query query = Query.builder().filter(filter).build();
    assertEquals(filter, query.getFilter().orElse(null), "Unexpected filter");
  }

  @Test
  void testSetLimit() {
    Limit limit = mock();
    Query query = Query.builder().limit(limit).build();
    assertEquals(limit, query.getLimit().orElse(null), "Unexpected limit");
  }

  @Test
  void testAddSortCriteria() {
    SortCriteria a = mock();
    SortCriteria b = mock();
    Query query = Query.builder().sort(a).sort(b).build();
    assertEquals(List.of(a, b), query.getSort(), "Unexpected sort");
  }

  @Test
  void testAddSortCriteriaAsCollection() {
    List<SortCriteria> a = List.of(mock(SortCriteria.class), mock(SortCriteria.class));
    List<SortCriteria> b = List.of(mock(SortCriteria.class));

    List<SortCriteria> expected = new ArrayList<>(a);
    expected.addAll(b);

    Query query = Query.builder().sort(a).sort(b).build();
    assertEquals(expected, query.getSort(), "Unexpected sort");
  }

  @Test
  void testAddSortCriteriaAsArray() {
    SortCriteria[] a = new SortCriteria[] {mock(SortCriteria.class), mock(SortCriteria.class)};
    SortCriteria[] b = new SortCriteria[] {mock(SortCriteria.class)};

    List<SortCriteria> expected = new ArrayList<>(Arrays.asList(a));
    expected.addAll(Arrays.asList(b));

    Query query = Query.builder().sort(a).sort(b).build();
    assertEquals(expected, query.getSort(), "Unexpected sort");
  }

  @Test
  void testToBuilder() {
    Filter filterA = mock();
    Limit limit = mock();
    SortCriteria sort = mock();

    Filter filterB = mock();

    Query query = Query.builder().filter(filterA).limit(limit).sort(sort).build();
    Query copy = query.toBuilder().filter(filterB).build();

    Query expected = Query.builder().filter(filterB).limit(limit).sort(sort).build();

    assertEquals(expected, copy, "Unexpected query");
  }
}
