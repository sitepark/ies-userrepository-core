package com.sitepark.ies.userrepository.core.usecase.user;

import com.sitepark.ies.sharedkernel.base.Identifier;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * Request to remove a single user from the repository.
 *
 * @param identifier the identifier (ID or anchor) of the user to remove
 */
public record RemoveUserRequest(@NotNull Identifier identifier) {

  /**
   * Creates a new builder for RemoveUserRequest.
   *
   * @return a new builder instance
   */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder for RemoveUserRequest. */
  public static final class Builder {

    private Identifier identifier;

    /**
     * Sets the identifier for the user to remove.
     *
     * @param identifier the user identifier (ID or anchor)
     * @return this builder
     */
    public Builder identifier(Identifier identifier) {
      this.identifier = identifier;
      return this;
    }

    /**
     * Sets the user ID to remove.
     *
     * @param id the user ID
     * @return this builder
     */
    public Builder id(String id) {
      this.identifier = Identifier.ofId(id);
      return this;
    }

    /**
     * Sets the user anchor to remove.
     *
     * @param anchor the user anchor
     * @return this builder
     */
    public Builder anchor(String anchor) {
      this.identifier = Identifier.ofAnchor(anchor);
      return this;
    }

    /**
     * Builds the RemoveUserRequest.
     *
     * @return the request instance
     * @throws NullPointerException if identifier is null
     */
    public RemoveUserRequest build() {
      Objects.requireNonNull(this.identifier, "identifier must not be null");
      return new RemoveUserRequest(this.identifier);
    }
  }
}
