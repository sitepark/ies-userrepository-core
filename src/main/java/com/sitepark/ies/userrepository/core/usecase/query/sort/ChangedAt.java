package com.sitepark.ies.userrepository.core.usecase.query.sort;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ChangedAt extends SortCriteria {

  public ChangedAt(Direction direction) {
    super(direction);
  }

  @Override
  public String toString() {
    return "ChangedAt [direction=" + getDirection() + "]";
  }
}
