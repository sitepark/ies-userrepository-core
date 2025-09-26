package com.sitepark.ies.userrepository.core.domain.value;

import com.sitepark.ies.sharedkernel.json.RawJson;

public record Permission(String type, @RawJson String data) {}
