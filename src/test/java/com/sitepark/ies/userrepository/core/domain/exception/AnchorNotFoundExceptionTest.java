package com.sitepark.ies.userrepository.core.domain.exception;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;
import org.junit.jupiter.api.Test;

class AnchorNotFoundExceptionTest {

  @Test
  void test() {
    Anchor anchor = Anchor.ofString("abc");
    AnchorNotFoundException e = new AnchorNotFoundException(anchor);
    assertEquals(anchor, e.getAnchor(), "unexpecatd anchor");
    assertThat("message should contains 'abc'", e.getMessage(), containsString(" abc "));
  }
}
