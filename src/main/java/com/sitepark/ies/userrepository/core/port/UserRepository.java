package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.usecase.query.Query;
import com.sitepark.ies.userrepository.core.usecase.query.Result;
import java.util.Optional;

public interface UserRepository {
  void create(User entity);

  void update(User entity);

  Optional<User> get(String id);

  Result<User> getAll(Query query);

  void remove(String id);

  Optional<String> resolveLogin(String login);

  Optional<String> resolveAnchor(Anchor anchor);
}
