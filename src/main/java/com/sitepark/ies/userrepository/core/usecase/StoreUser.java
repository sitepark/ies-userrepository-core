package com.sitepark.ies.userrepository.core.usecase;

import com.sitepark.ies.userrepository.core.domain.entity.Identifier;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.AccessDenied;
import com.sitepark.ies.userrepository.core.port.AccessControl;
import com.sitepark.ies.userrepository.core.port.IdGenerator;
import com.sitepark.ies.userrepository.core.port.UserRepository;

public final class StoreUser {

	private final UserRepository repository;
	private final AccessControl accessControl;
	private final IdGenerator idGenerator;

	protected StoreUser(UserRepository repository, AccessControl accessControl, IdGenerator idGenerator) {
		this.repository = repository;
		this.accessControl = accessControl;
		this.idGenerator = idGenerator;
	}

	public Identifier store(User entity) {
		if (entity.getIdentifier().isEmpty()) {
			return this.create(entity);
		} else {
			return this.update(entity);
		}
	}

	private Identifier create(User newEntity) {

		long generatedId = this.idGenerator.generate();

		User entityWithId = newEntity.toBuilder().identifier(Identifier.ofId(generatedId)).build();

		this.repository.store(entityWithId);

		return entityWithId.getIdentifier().get();
	}

	private Identifier update(User updateEntity) {

		updateEntity.getIdentifier()
				.orElseThrow(() -> new IllegalArgumentException("Update failed, identifier missing"));

		long id = this.repository.resolve(updateEntity.getIdentifier().get());

		if (!this.accessControl.isUserWritable(id)) {
			throw new AccessDenied("Not allowed to update user " + updateEntity.getIdentifier());
		}

		this.repository.store(updateEntity);

		return updateEntity.getIdentifier().get();
	}
}
