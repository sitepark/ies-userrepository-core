package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.userrepository.core.domain.value.Password;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface PasswordHasher {
  Password hash(String password);
}
