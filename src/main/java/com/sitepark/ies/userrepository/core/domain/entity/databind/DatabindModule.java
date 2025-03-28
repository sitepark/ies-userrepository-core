package com.sitepark.ies.userrepository.core.domain.entity.databind;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sitepark.ies.userrepository.core.domain.entity.query.filter.Filter;
import com.sitepark.ies.userrepository.core.domain.entity.query.filter.FilterDeserializer;

public class DatabindModule extends SimpleModule {

  private static final long serialVersionUID = 1L;

  public DatabindModule() {
    super.addDeserializer(Filter.class, new FilterDeserializer());
  }
}
