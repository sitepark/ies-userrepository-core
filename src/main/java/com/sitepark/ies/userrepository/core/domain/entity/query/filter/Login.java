package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public final class Login implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String login;

  Login(@JsonProperty("login") String login) {
    Objects.requireNonNull(login, "login is null");
    this.login = login;
  }

  public String getLogin() {
    return this.login;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.login);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Login that) && Objects.equals(this.login, that.login);
  }

  @Override
  public String toString() {
    return "Login{" + "login='" + login + '\'' + '}';
  }
}
