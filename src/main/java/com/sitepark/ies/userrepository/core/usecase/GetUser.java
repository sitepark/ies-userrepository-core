package com.sitepark.ies.userrepository.core.usecase;

import java.util.List;

import javax.inject.Inject;

import com.sitepark.ies.userrepository.core.domain.entity.Identifier;
import com.sitepark.ies.userrepository.core.domain.entity.Role;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.AccessDeniedException;
import com.sitepark.ies.userrepository.core.domain.exception.UserNotFoundException;
import com.sitepark.ies.userrepository.core.domain.service.IdentifierResolver;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.RoleAssigner;
import com.sitepark.ies.userrepository.core.port.UserRepository;

public final class GetUser {

	private final UserRepository repository;

	private final IdentifierResolver identifierResolver;

	private final RoleAssigner roleAssigner;

	private final AccessControl accessControl;

	@Inject
	protected GetUser(
			UserRepository repository,
			IdentifierResolver identifierResolver,
			RoleAssigner roleAssigner,
			AccessControl accessControl) {
		this.repository = repository;
		this.identifierResolver = identifierResolver;
		this.roleAssigner = roleAssigner;
		this.accessControl = accessControl;
	}

	public User getUser(Identifier identifier) {

		long id = this.identifierResolver.resolveIdentifier(identifier);

		if (!this.accessControl.isUserReadable(id)) {
			throw new AccessDeniedException("Not allowed to reat user " + id);
		}

		User user = this.repository.get(id).orElseThrow(() -> new UserNotFoundException(id));

		List<Role> roleList = this.roleAssigner.getRolesAssignByUser(id);

		return user.toBuilder()
				.roleList(roleList)
				.build();
	}
}
