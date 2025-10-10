package com.sitepark.ies.userrepository.core.domain.value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.concurrent.Immutable;

@Immutable
public class RoleUserAssignment {

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private final Map<String, Set<String>> assignments = new HashMap<>();

  @SuppressWarnings("PMD.LawOfDemeter")
  RoleUserAssignment(Builder builder) {
    builder.assignments.forEach((key, value) -> this.assignments.put(key, Set.copyOf(value)));
  }

  public List<String> roleIds() {
    return List.copyOf(this.assignments.keySet());
  }

  public List<String> userIds(String roleId) {
    Set<String> userIds = this.assignments.get(roleId);
    if (userIds == null) {
      return List.of();
    }
    return List.copyOf(userIds);
  }

  public List<String> userIds() {
    Set<String> allUserIds = new TreeSet<>();
    for (Set<String> userIds : this.assignments.values()) {
      allUserIds.addAll(userIds);
    }
    return List.copyOf(allUserIds);
  }

  public boolean isEmpty() {
    return this.assignments.isEmpty();
  }

  public int size() {
    return this.assignments.size();
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.assignments);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof RoleUserAssignment that)
        && Objects.equals(this.assignments, that.assignments);
  }

  @Override
  public String toString() {
    return "PrivilegeRoleAssignment{" + "assignments=" + assignments + '}';
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private final Map<String, Set<String>> assignments = new HashMap<>();

    private Builder() {}

    @SuppressWarnings("PMD.LawOfDemeter")
    private Builder(RoleUserAssignment userRoleAssignment) {
      userRoleAssignment.assignments.forEach(
          (key, value) -> this.assignments.put(key, new TreeSet<>(value)));
    }

    public Builder assignments(String roleId, List<String> userIds) {
      Objects.requireNonNull(userIds, "roleIds must not be null");
      for (String userId : userIds) {
        this.assignment(roleId, userId);
      }
      return this;
    }

    public Builder assignments(String roleId, String... userIds) {
      Objects.requireNonNull(userIds, "userIds must not be null");
      for (String userId : userIds) {
        this.assignment(roleId, userId);
      }
      return this;
    }

    public Builder assignment(String roleId, String userId) {
      Objects.requireNonNull(roleId, "roleIds must not be null");
      Objects.requireNonNull(userId, "roleId must not be null");

      Set<String> userIds = this.assignments.computeIfAbsent(roleId, k -> new TreeSet<>());
      userIds.add(userId);
      return this;
    }

    public RoleUserAssignment build() {
      return new RoleUserAssignment(this);
    }
  }
}
