package org.papertrail.sdk.model;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String timeStamp,
        String path
){}
