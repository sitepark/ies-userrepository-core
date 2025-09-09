package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.usecase.query.Query;
import com.sitepark.ies.userrepository.core.usecase.query.Result;
import com.sitepark.ies.userrepository.core.usecase.query.filter.Filter;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

  String create(User user);

  void update(User user);

  void remove(List<String> ids);

  Optional<User> get(String id);

  List<User> getAll(Filter filter);

  List<User> getByIds(List<String> ids);

  Result<User> search(Query query);

  Optional<String> resolveLogin(String login);

  Optional<String> resolveAnchor(Anchor anchor);
}
