package com.sitepark.ies.userrepository.core.domain.entity.query.sort;

public class LastName extends SortCriteria {

  public LastName(Direction direction) {
    super(direction);
  }

  @Override
  public String toString() {
    return "LastName [direction=" + getDirection() + "]";
  }
}
