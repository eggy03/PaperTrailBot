package org.papertrail.sdk.http;

import org.jspecify.annotations.Nullable;

public record HttpServiceResponse<S, E>(@Nullable S success, @Nullable E error, boolean requestSuccess) {

}
