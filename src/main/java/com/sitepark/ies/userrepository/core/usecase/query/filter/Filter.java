package com.sitepark.ies.userrepository.core.usecase.query.filter;

@SuppressWarnings("PMD.TooManyMethods")
public interface Filter {

  static Id id(String id) {
    return new Id(id);
  }

  static IdList idList(String... idList) {
    return new IdList(idList);
  }

  static Anchor anchor(com.sitepark.ies.sharedkernel.anchor.Anchor anchor) {
    return new Anchor(anchor);
  }

  static AnchorList anchorList(com.sitepark.ies.sharedkernel.anchor.Anchor... anchorList) {
    return new AnchorList(anchorList);
  }

  static FirstName firstName(String firstname) {
    return new FirstName(firstname);
  }

  static LastName lastName(String lastname) {
    return new LastName(lastname);
  }

  static Email email(String email) {
    return new Email(email);
  }

  static Login login(String login) {
    return new Login(login);
  }

  static RoleId roleId(String roleId) {
    return new RoleId(roleId);
  }

  static RoleIdList roleIdList(String... roleIdList) {
    return new RoleIdList(roleIdList);
  }

  static PrivilegeId privilegeId(String privilegeId) {
    return new PrivilegeId(privilegeId);
  }

  static PrivilegeIdList privilegeIdList(String... privilegeIdList) {
    return new PrivilegeIdList(privilegeIdList);
  }

  static Or or(Filter... filterList) {
    return new Or(filterList);
  }

  static And and(Filter... filterList) {
    return new And(filterList);
  }

  static Not not(Filter filter) {
    return new Not(filter);
  }
}
