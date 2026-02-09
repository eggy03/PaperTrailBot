package io.github.eggy03.papertrail.bot.commons.utils;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@UtilityClass
public class DurationUtils {

	public static String formatSeconds(Object seconds) {
		if (seconds == null) {
			return "ERROR: Value Returned Null";
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
            if(durationSeconds > 0)
                sb.append(durationSeconds).append("s");

			return sb.toString().trim();
		} catch (NumberFormatException e) {
			return "Duration Cannot Be Parsed";
		}
		
		
	}
	
	public static String formatMinutes(Object minutes) {
		if (minutes == null) {
			return "ERROR: Value Returned Null";
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
			return "Duration Cannot Be Parsed";
		}
		
		
	}
	
	public static String isoToLocalTimeCounter(Object isoTime) {
		String isoTimeString = String.valueOf(isoTime);
		if(isoTimeString==null || isoTimeString.equals("null") || isoTimeString.isBlank()) {
			return "N/A";
		}
		
		try {
			OffsetDateTime odt = OffsetDateTime.parse(isoTimeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
			long unixTimestamp = odt.toEpochSecond();
			return "<t:" + unixTimestamp + ":f>";
		} catch (DateTimeParseException e) {
			return "Could Not Parse Date And Time";
		}
		
	}
}
