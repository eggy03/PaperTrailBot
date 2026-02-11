package io.github.eggy03.papertrail.bot.commons.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@UtilityClass
@Slf4j
public class DurationUtils {

    public static String formatSeconds(@Nullable Object seconds) {
        if (seconds == null) {
            log.debug("seconds value is null");
            return "N/A";
        }

        try {
            long secondsLong = Long.parseLong(seconds.toString());
            if (secondsLong == 0L) return "No Limits";
            Duration d = Duration.ofSeconds(secondsLong);
            long durationDays = d.toDays();
            long durationHours = d.toHoursPart();
            long durationMinutes = d.toMinutesPart();
            long durationSeconds = d.toSecondsPart();

            StringBuilder sb = new StringBuilder();

            if (durationDays > 0)
                sb.append(durationDays).append("d ");
            if (durationHours > 0)
                sb.append(durationHours).append("h ");
            if (durationMinutes > 0)
                sb.append(durationMinutes).append("m ");
            if (durationSeconds > 0)
                sb.append(durationSeconds).append("s");

            return sb.toString().trim();
        } catch (NumberFormatException e) {
            log.debug("failed to parse seconds from value={}", seconds);
            return String.valueOf(seconds);
        }


    }

    public static String formatMinutes(@Nullable Object minutes) {
        if (minutes == null) {
            log.debug("minutes value is null");
            return "N/A";
        }
        try {
            long minutesLong = Long.parseLong(minutes.toString());
            if (minutesLong == 0L) return "No Limits";
            Duration d = Duration.ofMinutes(minutesLong);
            long days = d.toDays();
            long hours = d.toHoursPart();

            StringBuilder sb = new StringBuilder();
            if (days > 0) sb.append(days).append("d ");
            if (hours > 0) sb.append(hours).append("h ");
            return sb.toString().trim();
        } catch (NumberFormatException e) {
            log.debug("failed to parse minutes from value={}", minutes);
            return String.valueOf(minutes);
        }


    }

    public static String isoToLocalTimeCounter(@Nullable Object isoTime) {

        if (isoTime == null) {
            log.debug("iso time is null");
            return "N/A";
        }

        String isoTimeString = String.valueOf(isoTime);
        if (isoTimeString.trim().isEmpty()) {
            log.debug("iso time is blank");
            return "N/A";
        }

        try {
            OffsetDateTime odt = OffsetDateTime.parse(isoTimeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            long unixTimestamp = odt.toEpochSecond();
            return "<t:" + unixTimestamp + ":f>";
        } catch (DateTimeParseException e) {
            log.debug("failed to parse ISO_OFFSET_DATE_TIME from value={}", isoTimeString);
            return String.valueOf(isoTime);
        }

    }
}
