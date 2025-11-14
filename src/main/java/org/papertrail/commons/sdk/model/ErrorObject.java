package org.papertrail.commons.sdk.model;

public record ErrorObject(
        int status,
        String error,
        String message,
        String timeStamp,
        String path
){}
