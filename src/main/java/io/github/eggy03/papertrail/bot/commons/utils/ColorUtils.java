package io.github.eggy03.papertrail.bot.commons.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.IllegalFormatException;
import java.util.Map;

@UtilityClass
@Slf4j
public class ColorUtils {

    public static String formatToHex(@Nullable Object colorValueInteger) {

        if (colorValueInteger == null) {
            log.debug("colorValueInteger is null");
            return "N/A";
        }

        try {
            int color = Integer.parseInt(String.valueOf(colorValueInteger));
            return String.format("#%06X", color);
        } catch (NumberFormatException e) {
            log.debug("failed to parse color integer from value={}", colorValueInteger);
            return String.valueOf(colorValueInteger);
        } catch (IllegalFormatException e) {
            log.debug("failed to format color integer to hex, value={}", colorValueInteger);
            return String.valueOf(colorValueInteger);
        }
    }

    // gradient is returned as a hash map in the following structure
    // {"primary_color" = 123456789, "secondary_color" = 123456789, "tertiary_color" = 123456789}
    public static String formatGradientColorSystemToHex(@Nullable Object gradientMap) {

        if (gradientMap == null) {
            log.debug("color gradient map is null");
            return "N/A";
        }

        if (gradientMap instanceof Map<?, ?> colorMap) {

            String primaryColor = "Primary Color: " + formatToHex(colorMap.get("primary_color"));
            String secondaryColor = "Secondary Color: " + formatToHex(colorMap.get("secondary_color"));
            String tertiaryColor = "Tertiary Color: " + formatToHex(colorMap.get("tertiary_color"));

            return primaryColor + System.lineSeparator() + secondaryColor + System.lineSeparator() + tertiaryColor;
        }

        log.debug("gradient value is not a Map, value={}", gradientMap);
        return "N/A";
    }
}
