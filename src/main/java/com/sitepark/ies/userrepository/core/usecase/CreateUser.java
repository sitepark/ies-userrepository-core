package com.sitepark.ies.userrepository.core.usecase;

import java.util.Arrays;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.AccessDenied;
import com.sitepark.ies.userrepository.core.domain.exception.AnchorAlreadyExists;
import com.sitepark.ies.userrepository.core.domain.exception.LoginAlreadyExists;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.ExtensionsNotifier;
import com.sitepark.ies.userrepository.core.port.IdGenerator;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;

public final class CreateUser {

	private final UserRepository repository;

	private final RoleAssigner roleAssigner;

	private final AccessControl accessControl;

	private final IdGenerator idGenerator;

	private final ExtensionsNotifier extensionsNotifier;

	private static Logger LOGGER = LogManager.getLogger();

	@Inject
	protected CreateUser(
			UserRepository repository,
			RoleAssigner roleAssigner,
			AccessControl accessControl,
			IdGenerator idGenerator,
			ExtensionsNotifier extensionsNotifier) {
		this.repository = repository;
		this.roleAssigner = roleAssigner;
		this.accessControl = accessControl;
		this.idGenerator = idGenerator;
		this.extensionsNotifier = extensionsNotifier;
	}

	public long createUser(User newUser) {

		if (newUser.getId().isPresent()) {
			throw new IllegalArgumentException("The ID of the user must not be set when creating.");
		}

		this.validateAnchor(newUser);

		this.validateLogin(newUser);

		if (!this.accessControl.isUserCreateable()) {
			throw new AccessDenied("Not allowed to create user " + newUser);
		}

		long generatedId = this.idGenerator.generate();

		User userWithId = newUser.toBuilder().id(generatedId).build();

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("create user: {}", userWithId);
		}

		this.repository.create(userWithId);

		this.roleAssigner.assignRoleToUser(
				userWithId.getRoleList(),
				Arrays.asList(generatedId));

		this.extensionsNotifier.notifyCreated(userWithId);

		return userWithId.getId().get();
	}

	private void validateAnchor(User newUser) {
		if (newUser.getAnchor().isPresent()) {
			Optional<Long> anchorOwner = this.repository.resolveAnchor(newUser.getAnchor().get());
			if (anchorOwner.isPresent()) {
				throw new AnchorAlreadyExists(newUser.getAnchor().get(), anchorOwner.get());
			}
		}
	}

	private void validateLogin(User newUser) {
		Optional<Long> resolveLogin = this.repository.resolveLogin(newUser.getLogin());
		if (resolveLogin.isPresent()) {
			throw new LoginAlreadyExists(newUser.getLogin(), resolveLogin.get());
		}
	}
}
