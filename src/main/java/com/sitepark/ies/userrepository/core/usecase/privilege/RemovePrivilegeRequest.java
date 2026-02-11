package com.sitepark.ies.userrepository.core.usecase.privilege;

import com.sitepark.ies.sharedkernel.base.Identifier;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * Request to remove a single privilege from the repository.
 *
 * @param identifier the identifier (ID or anchor) of the privilege to remove
 */
public record RemovePrivilegeRequest(@NotNull Identifier identifier) {

  /**
   * Creates a new builder for RemovePrivilegeRequest.
   *
   * @return a new builder instance
   */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder for RemovePrivilegeRequest. */
  public static final class Builder {

    private Identifier identifier;

    /**
     * Sets the identifier for the privilege to remove.
     *
     * @param identifier the privilege identifier (ID or anchor)
     * @return this builder
     */
    public Builder identifier(Identifier identifier) {
      this.identifier = identifier;
      return this;
    }

    /**
     * Sets the privilege ID to remove.
     *
     * @param id the privilege ID
     * @return this builder
     */
    public Builder id(String id) {
      this.identifier = Identifier.ofId(id);
      return this;
    }

    /**
     * Sets the privilege anchor to remove.
     *
     * @param anchor the privilege anchor
     * @return this builder
     */
    public Builder anchor(String anchor) {
      this.identifier = Identifier.ofAnchor(anchor);
      return this;
    }

    /**
     * Builds the RemovePrivilegeRequest.
     *
     * @return the request instance
     * @throws NullPointerException if identifier is null
     */
    public RemovePrivilegeRequest build() {
      Objects.requireNonNull(this.identifier, "identifier must not be null");
      return new RemovePrivilegeRequest(this.identifier);
    }
  }
}
