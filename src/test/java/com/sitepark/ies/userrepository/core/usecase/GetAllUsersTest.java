package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.shared.security.exceptions.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import com.sitepark.ies.userrepository.core.usecase.query.Query;
import com.sitepark.ies.userrepository.core.usecase.query.Result;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetAllUsersTest {

  @Test
  void testAccessDenied() {

    UserRepository userRepository = mock();
    AccessControl accessControl = mock();
    when(accessControl.isUserReadable()).thenReturn(false);

    GetAllUsers getAllUsersUseCase = new GetAllUsers(userRepository, accessControl);

    assertThrows(
        AccessDeniedException.class, () -> getAllUsersUseCase.getAllUsers(Query.builder().build()));
  }

  @Test
  void testGet() {

    User user = User.builder().id("123").login("test").build();

    UserRepository userRepository = mock();
    when(userRepository.getAll(any())).thenReturn(new Result<>(List.of(user), 1, 0, 1));
    AccessControl accessControl = mock();
    when(accessControl.isUserReadable()).thenReturn(true);

    GetAllUsers getAllUsersUseCase = new GetAllUsers(userRepository, accessControl);

    assertEquals(
        new Result<>(List.of(user), 1, 0, 1),
        getAllUsersUseCase.getAllUsers(Query.builder().build()),
        "Unexpected result");
  }
}
