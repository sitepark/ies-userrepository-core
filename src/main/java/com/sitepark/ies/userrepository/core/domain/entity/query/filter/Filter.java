package com.sitepark.ies.userrepository.core.domain.entity.query.filter;

@SuppressWarnings("PMD.TooManyMethods")
public interface Filter {

  public static Id id(String id) {
    return new Id(id);
  }

  public static IdList idList(String... idlist) {
    return new IdList(idlist);
  }

  public static Anchor anchor(com.sitepark.ies.userrepository.core.domain.entity.Anchor anchor) {
    return new Anchor(anchor);
  }

  public static AnchorList anchorList(
      com.sitepark.ies.userrepository.core.domain.entity.Anchor... anchorList) {
    return new AnchorList(anchorList);
  }

  public static Firstname firstname(String firstname) {
    return new Firstname(firstname);
  }

  public static Lastname lastname(String lastname) {
    return new Lastname(lastname);
  }

  public static Email email(String email) {
    return new Email(email);
  }

  public static Login login(String login) {
    return new Login(login);
  }

  public static Or or(Filter... filterList) {
    return new Or(filterList);
  }

  public static And and(Filter... filterList) {
    return new And(filterList);
  }

  public static Not not(Filter filter) {
    return new Not(filter);
  }
}
