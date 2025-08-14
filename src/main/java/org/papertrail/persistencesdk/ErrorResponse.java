package org.papertrail.persistencesdk;

import java.time.LocalDateTime;

public record ErrorResponse(int status, String message, LocalDateTime timeStamp) {
}
