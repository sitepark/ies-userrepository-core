package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.sitepark.ies.userrepository.core.domain.entity.Identifier;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.entity.role.Ref;
import com.sitepark.ies.userrepository.core.domain.entity.role.UserLevelRoles;
import com.sitepark.ies.userrepository.core.domain.exception.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;

class GetUserTest {

	@Test
	void testAccessDeniedGetWithId() {

		UserRepository userRepository = mock();
		IdentifierResolver identifierResolver = mock();
		when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
		RoleAssigner roleAssigner = mock();
		AccessControl accessControl = mock();
		when(accessControl.isUserReadable("123")).thenReturn(false);

		GetUser getUserUseCase = new GetUser(
				userRepository,
				identifierResolver,
				roleAssigner,
				accessControl);

		assertThrows(AccessDeniedException.class, () -> {
			getUserUseCase.getUser(Identifier.ofString("123"));
		});
	}

	@Test
	void testAccessDeniedGetWithAnchor() {

		UserRepository userRepository = mock();
		IdentifierResolver identifierResolver = mock();
		when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
		RoleAssigner roleAssigner = mock();
		AccessControl accessControl = mock();
		when(accessControl.isUserReadable("123")).thenReturn(false);

		GetUser getUserUseCase = new GetUser(
				userRepository,
				identifierResolver,
				roleAssigner,
				accessControl);

		assertThrows(AccessDeniedException.class, () -> {
			getUserUseCase.getUser(Identifier.ofString("abc"));
		});
	}

	@Test
	void testGet() {

		UserRepository userRepository = mock();
		User storedUser = User.builder()
				.id("123")
				.login("test")
				.build();
		when(userRepository.get("123")).thenReturn(Optional.of(storedUser));
		IdentifierResolver identifierResolver = mock();
		when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
		RoleAssigner roleAssigner = mock();
		when(roleAssigner.getRolesAssignByUser("123")).thenReturn(Arrays.asList(
				UserLevelRoles.USER,
				Ref.ofAnchor("role.a")
		));
		AccessControl accessControl = mock();
		when(accessControl.isUserReadable(anyString())).thenReturn(true);

		GetUser getUserUseCase = new GetUser(
				userRepository,
				identifierResolver,
				roleAssigner,
				accessControl);

		User expectedUser = User.builder()
				.id("123")
				.login("test")
				.roleList(UserLevelRoles.USER, Ref.ofAnchor("role.a"))
				.build();

		User user = getUserUseCase.getUser(Identifier.ofString("123"));

		assertEquals(expectedUser, user, "unexpected user");
	}

	@Test
	void testGetUserNotFound() {

		UserRepository userRepository = mock();
		IdentifierResolver identifierResolver = mock();
		when(identifierResolver.resolveIdentifier(any())).thenReturn("123");
		RoleAssigner roleAssigner = mock();
		AccessControl accessControl = mock();
		when(accessControl.isUserReadable(anyString())).thenReturn(true);

		GetUser getUserUseCase = new GetUser(
				userRepository,
				identifierResolver,
				roleAssigner,
				accessControl);

		UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> {
			getUserUseCase.getUser(Identifier.ofString("123"));
		});
		assertEquals("123", e.getId(), "unexpected user");
		assertNotNull(e.getMessage(), "message is null");
	}
}
