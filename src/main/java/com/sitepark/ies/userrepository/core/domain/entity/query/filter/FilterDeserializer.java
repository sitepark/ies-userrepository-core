package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.sitepark.ies.userrepository.core.domain.entity.databind.UniquePropertyPolymorphicDeserializer;

public class FilterDeserializer extends UniquePropertyPolymorphicDeserializer<Filter> {

  private static final long serialVersionUID = 1L;

  public FilterDeserializer() {
    super(Filter.class);
    super.register("id", Id.class);
    super.register("idList", IdList.class);
    super.register("anchor", Anchor.class);
    super.register("anchorList", AnchorList.class);
    super.register("firstName", FirstName.class);
    super.register("lastName", LastName.class);
    super.register("email", Email.class);
    super.register("login", Login.class);
    super.register("and", And.class);
    super.register("or", Or.class);
    super.register("not", Not.class);
  }
}
