package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.entity.role.Ref;
import com.sitepark.ies.userrepository.core.domain.entity.role.UserLevelRoles;
import com.sitepark.ies.userrepository.core.domain.exception.AccessDenied;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFound;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;

class GetUserTest {

	@Test
	void testAccessDeniedGet() {

		UserRepository userRepository = mock();
		RoleAssigner roleAssigner = mock();
		AccessControl accessControl = mock();
		when(accessControl.isUserReadable(123L)).thenReturn(false);

		GetUser getUserUseCase = new GetUser(
				userRepository,
				roleAssigner,
				accessControl);

		assertThrows(AccessDenied.class, () -> {
			getUserUseCase.getUser(123L);
		});
	}

	@Test
	void testGet() {

		UserRepository userRepository = mock();
		User storedUser = User.builder()
				.id(123L)
				.login("test")
				.build();
		when(userRepository.get(123L)).thenReturn(Optional.of(storedUser));
		RoleAssigner roleAssigner = mock();
		when(roleAssigner.getRolesAssignByUser(123L)).thenReturn(Arrays.asList(
				UserLevelRoles.USER,
				Ref.ofAnchor("role.a")
		));
		AccessControl accessControl = mock();
		when(accessControl.isUserReadable(anyLong())).thenReturn(true);

		GetUser getUserUseCase = new GetUser(
				userRepository,
				roleAssigner,
				accessControl);

		User expectedUser = User.builder()
				.id(123L)
				.login("test")
				.roleList(UserLevelRoles.USER, Ref.ofAnchor("role.a"))
				.build();

		User user = getUserUseCase.getUser(123L);

		assertEquals(expectedUser, user, "unexpected user");
	}

	@Test
	void testGetUserNotFound() {

		UserRepository userRepository = mock();
		RoleAssigner roleAssigner = mock();
		AccessControl accessControl = mock();
		when(accessControl.isUserReadable(anyLong())).thenReturn(true);

		GetUser getUserUseCase = new GetUser(
				userRepository,
				roleAssigner,
				accessControl);

		assertThrows(UserNotFound.class, () -> {
			getUserUseCase.getUser(123L);
		});
	}
}
