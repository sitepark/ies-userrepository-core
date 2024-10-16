package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.userrepository.core.domain.entity.Password;

public interface PasswordHasher {
  Password hash(String password);
}
