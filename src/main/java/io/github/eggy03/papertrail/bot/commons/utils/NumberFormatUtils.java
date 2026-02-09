package io.github.eggy03.papertrail.bot.commons.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class NumberFormatUtils {

    // INTERNAL HELPERS
    @Nullable
    public static Long parseLong(@Nullable Object possibleLongValue) {
        if (possibleLongValue == null) return null;
        try {
            return Long.parseLong(String.valueOf(possibleLongValue));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static Integer parseInt(@Nullable Object possibleIntegerValue) {
        if (possibleIntegerValue == null) return null;
        try {
            return Integer.parseInt(String.valueOf(possibleIntegerValue));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
