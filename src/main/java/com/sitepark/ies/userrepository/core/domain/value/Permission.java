package com.sitepark.ies.userrepository.core.domain.value;

import com.sitepark.ies.sharedkernel.json.RawJson;
import javax.annotation.concurrent.Immutable;

@Immutable
public record Permission(String type, @RawJson String data) {}
