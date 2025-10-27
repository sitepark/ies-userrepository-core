package com.sitepark.ies.userrepository.core.usecase.query.sort;

import javax.annotation.concurrent.Immutable;

@Immutable
public class CreatedAt extends SortCriteria {

  public CreatedAt(Direction direction) {
    super(direction);
  }

  @Override
  public String toString() {
    return "CreatedAt [direction=" + getDirection() + "]";
  }
}
