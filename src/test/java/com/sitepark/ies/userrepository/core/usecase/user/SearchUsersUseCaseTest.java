package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.service.AccessControl;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import com.sitepark.ies.userrepository.core.usecase.query.Query;
import com.sitepark.ies.userrepository.core.usecase.query.Result;
import java.util.List;
import org.junit.jupiter.api.Test;

class SearchUsersUseCaseTest {
  @Test
  void testAccessDenied() {

    UserRepository userRepository = mock();
    AccessControl accessControl = mock();
    when(accessControl.isUserReadable()).thenReturn(false);

    SearchUsersUseCase searchUsers = new SearchUsersUseCase(userRepository, accessControl);

    assertThrows(
        AccessDeniedException.class, () -> searchUsers.searchUsers(Query.builder().build()));
  }

  @Test
  void testGet() {

    User user = User.builder().id("123").login("test").build();

    UserRepository userRepository = mock();
    when(userRepository.search(any())).thenReturn(new Result<>(List.of(user), 1, 0, 1));
    AccessControl accessControl = mock();
    when(accessControl.isUserReadable()).thenReturn(true);

    SearchUsersUseCase searchUsers = new SearchUsersUseCase(userRepository, accessControl);

    assertEquals(
        new Result<>(List.of(user), 1, 0, 1),
        searchUsers.searchUsers(Query.builder().build()),
        "Unexpected result");
  }
}
