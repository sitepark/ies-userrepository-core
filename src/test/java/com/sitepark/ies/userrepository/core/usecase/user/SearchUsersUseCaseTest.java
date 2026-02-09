package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.service.UserEntityAuthorizationService;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import com.sitepark.ies.userrepository.core.usecase.query.Query;
import com.sitepark.ies.userrepository.core.usecase.query.Result;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SearchUsersUseCaseTest {

  private UserRepository repository;
  private UserEntityAuthorizationService userAuthorizationService;
  private SearchUsersUseCase usecase;

  @BeforeEach
  void setUp() {
    this.repository = mock();
    this.userAuthorizationService = mock();
    this.usecase = new SearchUsersUseCase(repository, userAuthorizationService);
  }

  @Test
  void testAccessDenied() {
    User user = User.builder().id("123").login("test").build();
    when(repository.search(any())).thenReturn(new Result<>(List.of(user), 1, 0, 1));
    when(userAuthorizationService.isReadable(anyList())).thenReturn(false);
    assertThrows(AccessDeniedException.class, () -> usecase.searchUsers(Query.builder().build()));
  }

  @Test
  void testGet() {

    User user = User.builder().id("123").login("test").build();

    when(repository.search(any())).thenReturn(new Result<>(List.of(user), 1, 0, 1));
    when(userAuthorizationService.isReadable(anyList())).thenReturn(true);

    assertEquals(
        new Result<>(List.of(user), 1, 0, 1),
        usecase.searchUsers(Query.builder().build()),
        "Unexpected result");
  }
}
