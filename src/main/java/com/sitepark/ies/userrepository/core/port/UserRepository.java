package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.sharedkernel.anchor.domain.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.usecase.query.Query;
import com.sitepark.ies.userrepository.core.usecase.query.Result;
import com.sitepark.ies.userrepository.core.usecase.query.filter.Filter;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
  void create(User entity);

  void update(User entity);

  Optional<User> get(String id);

  List<User> getAll(Filter filter);

  Result<User> search(Query query);

  void remove(String id);

  Optional<String> resolveLogin(String login);

  Optional<String> resolveAnchor(Anchor anchor);
}
