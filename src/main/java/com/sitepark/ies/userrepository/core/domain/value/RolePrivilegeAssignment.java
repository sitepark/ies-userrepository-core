package com.sitepark.ies.userrepository.core.domain.value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.concurrent.Immutable;

@Immutable
public class RolePrivilegeAssignment {

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private final Map<String, Set<String>> assignments = new HashMap<>();

  @SuppressWarnings("PMD.LawOfDemeter")
  RolePrivilegeAssignment(Builder builder) {
    builder.assignments.forEach((key, value) -> this.assignments.put(key, Set.copyOf(value)));
  }

  public List<String> getRoleIds() {
    return List.copyOf(this.assignments.keySet());
  }

  public List<String> getPrivilegeIds(String roleId) {
    Set<String> privilegeIds = this.assignments.get(roleId);
    if (privilegeIds == null) {
      return List.of();
    }
    return List.copyOf(privilegeIds);
  }

  public List<String> getAllPrivilegeIds() {
    Set<String> allPrivilegeIds = new TreeSet<>();
    for (Set<String> roleIds : this.assignments.values()) {
      allPrivilegeIds.addAll(roleIds);
    }
    return List.copyOf(allPrivilegeIds);
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
    return (o instanceof RolePrivilegeAssignment that)
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
    private Builder(RolePrivilegeAssignment privilegeRoleAssignment) {
      privilegeRoleAssignment.assignments.forEach(
          (key, value) -> this.assignments.put(key, new TreeSet<>(value)));
    }

    public Builder assignments(String roleId, List<String> privilegesIds) {
      Objects.requireNonNull(privilegesIds, "privilegesIds must not be null");
      for (String privilegesId : privilegesIds) {
        this.assignment(roleId, privilegesId);
      }
      return this;
    }

    public Builder assignments(String roleId, String... privilegesIds) {
      Objects.requireNonNull(privilegesIds, "privilegesIds must not be null");
      for (String privilegesId : privilegesIds) {
        this.assignment(roleId, privilegesId);
      }
      return this;
    }

    public Builder assignment(String roleId, String privilegesId) {
      Objects.requireNonNull(roleId, "roleId must not be null");
      Objects.requireNonNull(privilegesId, "privilegesId must not be null");

      Set<String> privilegesIds = this.assignments.computeIfAbsent(roleId, k -> new TreeSet<>());
      privilegesIds.add(privilegesId);
      return this;
    }

    public RolePrivilegeAssignment build() {
      return new RolePrivilegeAssignment(this);
    }
  }
}
