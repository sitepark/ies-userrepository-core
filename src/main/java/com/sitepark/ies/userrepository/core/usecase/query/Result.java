package com.sitepark.ies.userrepository.core.usecase.query;

import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public record Result<T>(List<T> items, int total, int offset, int limit) {
  public Result {
    items = List.copyOf(items);
  }
}
