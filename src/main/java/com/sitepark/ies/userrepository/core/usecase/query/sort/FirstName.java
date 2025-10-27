package com.sitepark.ies.userrepository.core.usecase.query.sort;

import javax.annotation.concurrent.Immutable;

@Immutable
public class FirstName extends SortCriteria {

  public FirstName(Direction direction) {
    super(direction);
  }

  @Override
  public String toString() {
    return "FirstName [direction=" + getDirection() + "]";
  }
}
