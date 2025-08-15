package org.papertrail.sdk.model;

public record ErrorResponse(int status, String message, String timeStamp) {
}
