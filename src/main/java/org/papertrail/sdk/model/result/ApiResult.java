package org.papertrail.sdk.model.result;

public record ApiResult<S, E>(S success, E error) {

    public boolean isSuccess() {
        return success != null;
    }

    public boolean isError() {
        return error != null;
    }
}
