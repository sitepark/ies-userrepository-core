package com.sitepark.ies.userrepository.core.usecase.query;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.ies.userrepository.core.usecase.query.filter.Filter;
import com.sitepark.ies.userrepository.core.usecase.query.limit.Limit;
import com.sitepark.ies.userrepository.core.usecase.query.sort.SortCriteria;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

@JsonDeserialize(builder = Query.Builder.class)
@SuppressWarnings("PMD.LawOfDemeter")
public final class Query {

  private final @Nullable Filter filter;

  private final List<SortCriteria> sort;

  private final @Nullable Limit limit;

  private Query(Builder builder) {
    this.filter = builder.filter;
    this.sort = List.copyOf(builder.sort);
    this.limit = builder.limit;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Nullable
  public Filter getFilter() {
    return this.filter;
  }

  public List<SortCriteria> getSort() {
    return List.copyOf(this.sort);
  }

  @Nullable
  public Limit getLimit() {
    return this.limit;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.filter, this.sort, this.limit);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Query that)
        && Objects.equals(this.filter, that.filter)
        && Objects.equals(this.sort, that.sort)
        && Objects.equals(this.limit, that.limit);
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {

    protected @Nullable Filter filter;

    protected List<SortCriteria> sort = new ArrayList<>();

    protected @Nullable Limit limit;

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
