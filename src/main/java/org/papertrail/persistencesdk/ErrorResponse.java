package org.papertrail.persistencesdk;

public record ErrorResponse(int status, String message, String timeStamp) {
}
