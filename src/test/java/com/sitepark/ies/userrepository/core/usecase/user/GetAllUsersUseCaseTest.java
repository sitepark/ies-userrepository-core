package com.sitepark.ies.userrepository.core.usecase.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.sharedkernel.security.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.service.AccessControl;
import com.sitepark.ies.userrepository.core.port.UserRepository;
import com.sitepark.ies.userrepository.core.usecase.query.filter.Filter;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetAllUsersUseCaseTest {

  @Test
  void testAccessDenied() {

    UserRepository userRepository = mock();
    AccessControl accessControl = mock();
    when(accessControl.isUserReadable()).thenReturn(false);

    GetAllUsersUseCase getAllUsersUseCase = new GetAllUsersUseCase(userRepository, accessControl);

    assertThrows(
        AccessDeniedException.class, () -> getAllUsersUseCase.getAllUsers(Filter.id("123")));
  }

  @Test
  void testGet() {

    User user = User.builder().id("123").login("test").build();

    UserRepository userRepository = mock();
    when(userRepository.getAll(any())).thenReturn(List.of(user));
    AccessControl accessControl = mock();
    when(accessControl.isUserReadable()).thenReturn(true);

    GetAllUsersUseCase getAllUsersUseCase = new GetAllUsersUseCase(userRepository, accessControl);

    assertEquals(
        List.of(user), getAllUsersUseCase.getAllUsers(Filter.id("123")), "Unexpected result");
  }
}
