package com.sitepark.ies.userrepository.core.domain.service;

import java.util.Optional;

import javax.inject.Inject;

import com.sitepark.ies.userrepository.core.domain.entity.Identifier;
import com.sitepark.ies.userrepository.core.domain.exception.AnchorNotFoundException;
import com.sitepark.ies.userrepository.core.port.UserRepository;

public class IdentifierResolver {

	private final UserRepository repository;

	@Inject
	protected IdentifierResolver(UserRepository repository) {
		this.repository = repository;
	}

	public long resolveIdentifier(Identifier identifier) {

		if (identifier.getId().isPresent()) {
			return identifier.getId().get();
		}

		Optional<Long> id = this.repository.resolveAnchor(identifier.getAnchor().get());
		if (id.isEmpty()) {
			throw new AnchorNotFoundException(identifier.getAnchor().get());
		}
		return id.get();
	}
}
