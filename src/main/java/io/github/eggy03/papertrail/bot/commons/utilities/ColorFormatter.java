package io.github.eggy03.papertrail.bot.commons.utilities;

import lombok.experimental.UtilityClass;

import java.util.IllegalFormatException;
import java.util.Map;

@UtilityClass
public class ColorFormatter {

	public static String formatToHex(Object colorValueInteger) {
		
		if(colorValueInteger==null)
			return "N/A";
		
		try {
			int color = Integer.parseInt(String.valueOf(colorValueInteger));
			return String.format("#%06X", color);
		} catch (NumberFormatException e ) {
			return "No Parsable Color Integer Detected";
		} catch  (IllegalFormatException e) {
			return "Cannot parse color to hex";
		}
	}

    // gradient is returned as a hash map in the following structure
    // {"primary_color" = 123456789, "secondary_color" = 123456789, "tertiary_color" = 123456789}
	public static String formatGradientColorSystemToHex(Object gradientMap) {

        if(gradientMap instanceof Map<?, ?> colorMap){

            String primaryColor = "Primary Color: "+ formatToHex(colorMap.get("primary_color"));
            String secondaryColor = "Secondary Color: "+ formatToHex(colorMap.get("secondary_color"));
            String tertiaryColor = "Tertiary Color: "+ formatToHex(colorMap.get("tertiary_color"));

            return primaryColor+System.lineSeparator()+secondaryColor+System.lineSeparator()+tertiaryColor;
        }

		return "N/A";

	}
}
