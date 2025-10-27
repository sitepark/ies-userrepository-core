package com.sitepark.ies.userrepository.core.usecase.query.sort;

import javax.annotation.concurrent.Immutable;

@Immutable
public class LastName extends SortCriteria {

  public LastName(Direction direction) {
    super(direction);
  }

  @Override
  public String toString() {
    return "LastName [direction=" + getDirection() + "]";
  }
}
