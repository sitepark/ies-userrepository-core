/**
 * This module contains the essential business logic and data structures, of the user repository.
 */
module com.sitepark.ies.userrepository.core {
  exports com.sitepark.ies.userrepository.core.domain.entity;
  exports com.sitepark.ies.userrepository.core.domain.value.identity;
  exports com.sitepark.ies.userrepository.core.domain.value;
  exports com.sitepark.ies.userrepository.core.usecase.query;
  exports com.sitepark.ies.userrepository.core.usecase.query.sort;
  exports com.sitepark.ies.userrepository.core.usecase.query.filter;
  exports com.sitepark.ies.userrepository.core.usecase.query.limit;
  exports com.sitepark.ies.userrepository.core.domain.exception;
  exports com.sitepark.ies.userrepository.core.port;
  exports com.sitepark.ies.userrepository.core.usecase;
  exports com.sitepark.ies.userrepository.core.api;

  requires jakarta.inject;
  requires com.fasterxml.jackson.datatype.jdk8;
  requires com.fasterxml.jackson.datatype.jsr310;
  requires com.sitepark.ies.sharedkernel;
  requires org.apache.logging.log4j;
  requires static com.github.spotbugs.annotations;
  requires com.fasterxml.jackson.annotation;
  requires static org.jetbrains.annotations;
  requires com.fasterxml.jackson.databind;

  opens com.sitepark.ies.userrepository.core.domain.entity;
  opens com.sitepark.ies.userrepository.core.usecase.query;
  opens com.sitepark.ies.userrepository.core.usecase.query.sort;
  opens com.sitepark.ies.userrepository.core.usecase.query.filter;
  opens com.sitepark.ies.userrepository.core.usecase.query.limit;
  opens com.sitepark.ies.userrepository.core.domain.value.identity;
  opens com.sitepark.ies.userrepository.core.domain.value;
  opens com.sitepark.ies.userrepository.core.usecase;
}
