package io.github.eggy03.papertrail.bot.commons.utilities;

import lombok.experimental.UtilityClass;

/*
 * Resolves Object subtypes from their given IDs
 * @see <a href="https://discord.com/developers/docs/resources/channel#channel-object-channel-types"> Discord Channel Types </a>
 */
@UtilityClass
public class TypeResolver {


	public static String scheduleEventTypeResolver(Object type) {
		if (type == null) {
			return "ERROR: Value Returned Null";
		}

		try {
			int t = Integer.parseInt(type.toString());
			return switch (t) {
			case 1 -> "Stage Event";
			case 2 -> "Voice Event";
			case 3 -> "Text Event";
			default -> "Unknown";
			};
		} catch (NumberFormatException e) {
			return "No Parsable Type Values Detected";
		}

	}
	
	public static String scheduleEventStatusTypeResolver(Object type) {
		if (type == null) {
			return "ERROR: Value Returned Null";
		}

		try {
			int t = Integer.parseInt(type.toString());
			return switch (t) {
			case 1 -> "Scheduled";
			case 2 -> "Active";
			case 3 -> "Completed";
			case 4 -> "Cancelled";
			default -> "Unknown";
			};
		} catch (NumberFormatException e) {
			return "No Parsable Type Values Detected";
		}

	}
}
