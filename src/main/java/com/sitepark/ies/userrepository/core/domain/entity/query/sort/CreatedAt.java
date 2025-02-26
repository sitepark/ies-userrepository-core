package com.sitepark.ies.userrepository.core.domain.entity.query.sort;

public class CreatedAt extends SortCriteria {

  public CreatedAt(Direction direction) {
    super(direction);
  }

  @Override
  public String toString() {
    return "CreatedAt [direction=" + getDirection() + "]";
  }
}
