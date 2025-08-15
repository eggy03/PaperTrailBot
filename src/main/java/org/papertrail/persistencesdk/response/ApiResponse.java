package org.papertrail.persistencesdk.response;

public record ApiResponse<S, E>(S success, E error) {

    public boolean isSuccess() {
        return success != null;
    }

    public boolean isError() {
        return error != null;
    }
}
