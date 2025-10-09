package com.sitepark.ies.userrepository.core.port;

import com.sitepark.ies.sharedkernel.anchor.Anchor;
import java.util.Optional;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface AnchorResolver {
  Optional<String> resolveAnchor(Anchor anchor);
}
