package com.sitepark.ies.userrepository.core.domain.entity.query.sort;

public class ChangedAt extends SortCriteria {

  public ChangedAt(Direction direction) {
    super(direction);
  }

  @Override
  public String toString() {
    return "ChangedAt [direction=" + getDirection() + "]";
  }
}
