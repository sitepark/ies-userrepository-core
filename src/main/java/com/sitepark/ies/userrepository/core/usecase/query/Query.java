package com.sitepark.ies.userrepository.core.usecase.query;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.userrepository.core.usecase.query.filter.Filter;
import com.sitepark.ies.userrepository.core.usecase.query.limit.Limit;
import com.sitepark.ies.userrepository.core.usecase.query.sort.SortCriteria;
import java.util.*;

@JsonDeserialize(builder = Query.Builder.class)
public final class Query {

  private final Filter filter;

  private final List<SortCriteria> sort;

  private final Limit limit;

  private Query(Builder builder) {
    this.filter = builder.filter;
    this.sort = List.copyOf(builder.sort);
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

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {

    protected Filter filter;

    protected List<SortCriteria> sort = new ArrayList<>();

    protected Limit limit;

    protected Builder() {}

    protected Builder(Query query) {
      this.filter = query.filter;
      this.sort.addAll(query.sort);
      this.limit = query.limit;
    }

    public Builder filter(Filter filter) {
      this.filter = filter;
      return this;
    }

    public Builder sort(SortCriteria... sortCriteria) {
      Objects.requireNonNull(sortCriteria, "sortCriteria is null");
      this.sort.addAll(Arrays.asList(sortCriteria));
      for (SortCriteria sortCriterion : sortCriteria) {
        Objects.requireNonNull(sortCriterion, "sortCriterion contains null");
      }
      return this;
    }

    public Builder sort(Collection<SortCriteria> sortCriteria) {
      Objects.requireNonNull(sortCriteria, "sortCriteria is null");
      for (SortCriteria sortCriterion : sortCriteria) {
        this.sort(sortCriterion);
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
