package org.papertrail.sdk.response;

public record ErrorResponseObject(int status, String message, String timeStamp) {
}
