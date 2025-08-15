package org.papertrail.persistencesdk.response;

public record ErrorResponseObject(int status, String message, String timeStamp) {
}
