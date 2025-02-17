package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class Login implements Filter {

  @SuppressWarnings(
      "PMD.AvoidFieldNameMatchingTypeName") // so that when deserializing it has the desired format
  private final String login;

  protected Login(@JsonProperty("login") String login) {
    Objects.requireNonNull(login, "login is null");
    this.login = login;
  }

  public String getLogin() {
    return this.login;
  }
}
