package com.sitepark.ies.userrepository.core.usecase.role;

import com.sitepark.ies.sharedkernel.base.Identifier;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * Request to remove a single role from the repository.
 *
 * @param identifier the identifier (ID or anchor) of the role to remove
 */
public record RemoveRoleRequest(@NotNull Identifier identifier) {

  /**
   * Creates a new builder for RemoveRoleRequest.
   *
   * @return a new builder instance
   */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder for RemoveRoleRequest. */
  public static final class Builder {

    private Identifier identifier;

    /**
     * Sets the identifier for the role to remove.
     *
     * @param identifier the role identifier (ID or anchor)
     * @return this builder
     */
    public Builder identifier(Identifier identifier) {
      this.identifier = identifier;
      return this;
    }

    /**
     * Sets the role ID to remove.
     *
     * @param id the role ID
     * @return this builder
     */
    public Builder id(String id) {
      this.identifier = Identifier.ofId(id);
      return this;
    }

    /**
     * Sets the role anchor to remove.
     *
     * @param anchor the role anchor
     * @return this builder
     */
    public Builder anchor(String anchor) {
      this.identifier = Identifier.ofAnchor(anchor);
      return this;
    }

    /**
     * Builds the RemoveRoleRequest.
     *
     * @return the request instance
     * @throws NullPointerException if identifier is null
     */
    public RemoveRoleRequest build() {
      Objects.requireNonNull(this.identifier, "identifier must not be null");
      return new RemoveRoleRequest(this.identifier);
    }
  }
}
