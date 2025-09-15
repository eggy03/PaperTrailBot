package org.papertrail.sdk.model;

public record ErrorObject(
        int status,
        String error,
        String message,
        String timeStamp,
        String path
){}
