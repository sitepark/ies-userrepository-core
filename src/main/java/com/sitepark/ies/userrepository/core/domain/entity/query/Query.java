package com.sitepark.ies.userrepository.core.domain.entity.query;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.userrepository.core.domain.entity.query.filter.Filter;
import com.sitepark.ies.userrepository.core.domain.entity.query.limit.Limit;
import com.sitepark.ies.userrepository.core.domain.entity.query.sort.SortCriteria;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonDeserialize(builder = Query.Builder.class)
public class Query {

  private final Filter filter;

  private final List<SortCriteria> sort;

  private final Limit limit;

  protected Query(Builder builder) {
    this.filter = builder.filter;
    this.sort = Collections.unmodifiableList(builder.sort);
    this.limit = builder.limit;
  }

  public Optional<Filter> getFilter() {
    return Optional.ofNullable(this.filter);
  }

  public List<SortCriteria> getSort() {
    return this.sort;
  }

  public Optional<Limit> getLimit() {
    return Optional.ofNullable(this.limit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.filter, this.sort, this.limit);
  }

  @Override
  public boolean equals(Object o) {

    if (!(o instanceof Query that)) {
      return false;
    }

    return Objects.equals(this.filter, that.filter)
        && Objects.equals(this.sort, that.sort)
        && Objects.equals(this.limit, that.limit);
  }

  public static Builder builder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
  public static class Builder {

    protected Filter filter;

    protected List<SortCriteria> sort = new ArrayList<>();

    protected Limit limit;

    protected Builder() {}

    protected Builder(Query query) {
      this.filter = query.filter;
      this.sort = new ArrayList<>(query.sort);
      this.limit = query.limit;
    }

    public Builder filter(Filter filterBy) {
      this.filter = filterBy;
      return this;
    }

    public Builder sort(SortCriteria sortCriteria) {
      Objects.requireNonNull(sortCriteria, "sortCriteria is null");
      this.sort.add(sortCriteria);
      return this;
    }

    public Builder sort(SortCriteria[] sortCriterias) {
      Objects.requireNonNull(sortCriterias, "sortCriterias is null");
      for (SortCriteria criteria : sortCriterias) {
        this.sort(criteria);
      }
      return this;
    }

    public Builder sort(Collection<SortCriteria> sortCriterias) {
      Objects.requireNonNull(sortCriterias, "sortCriterias is null");
      for (SortCriteria criteria : sortCriterias) {
        this.sort(criteria);
      }
      return this;
    }

    public Builder limit(Limit limit) {
      Objects.requireNonNull(limit, "limit is null");
      this.limit = limit;
      return this;
    }

    public Query build() {
      return new Query(this);
    }
  }
}
