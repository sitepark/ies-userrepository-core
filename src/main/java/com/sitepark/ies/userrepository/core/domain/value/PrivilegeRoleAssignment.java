package com.sitepark.ies.userrepository.core.domain.value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.concurrent.Immutable;

@Immutable
public class PrivilegeRoleAssignment {

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private final Map<String, Set<String>> assignments = new HashMap<>();

  @SuppressWarnings("PMD.LawOfDemeter")
  PrivilegeRoleAssignment(Builder builder) {
    builder.assignments.forEach((key, value) -> this.assignments.put(key, Set.copyOf(value)));
  }

  public List<String> getPrivilegeIds() {
    return List.copyOf(this.assignments.keySet());
  }

  public List<String> getRoleIds(String privilegeId) {
    Set<String> roleIds = this.assignments.get(privilegeId);
    if (roleIds == null) {
      return List.of();
    }
    return List.copyOf(roleIds);
  }

  public List<String> getAllRoleIds() {
    Set<String> allRoleIds = new TreeSet<>();
    for (Set<String> roleIds : this.assignments.values()) {
      allRoleIds.addAll(roleIds);
    }
    return List.copyOf(allRoleIds);
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
    return (o instanceof PrivilegeRoleAssignment that)
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
    private Builder(PrivilegeRoleAssignment privilegeRoleAssignment) {
      privilegeRoleAssignment.assignments.forEach(
          (key, value) -> this.assignments.put(key, new TreeSet<>(value)));
    }

    public Builder assignments(String privilegeId, List<String> roleIds) {
      Objects.requireNonNull(roleIds, "roleIds must not be null");
      for (String roleId : roleIds) {
        this.assignment(privilegeId, roleId);
      }
      return this;
    }

    public Builder assignments(String privilegeId, String... roleIds) {
      Objects.requireNonNull(roleIds, "roleIds must not be null");
      for (String roleId : roleIds) {
        this.assignment(privilegeId, roleId);
      }
      return this;
    }

    public Builder assignment(String privilegeId, String roleId) {
      Objects.requireNonNull(privilegeId, "privilegeId must not be null");
      Objects.requireNonNull(roleId, "roleId must not be null");

      Set<String> roleIds = this.assignments.computeIfAbsent(privilegeId, k -> new TreeSet<>());
      roleIds.add(roleId);
      return this;
    }

    public PrivilegeRoleAssignment build() {
      return new PrivilegeRoleAssignment(this);
    }
  }
}
