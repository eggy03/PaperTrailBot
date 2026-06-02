package io.github.eggy03.papertrail.bot.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public final class BooleanUtils {

    @NonNull
    private static final String FALLBACK_STRING = "N/A";

    @NonNull
    public static String formatToEmoji(@Nullable Object booleanValueObject) {

        if (booleanValueObject == null) {
            return FALLBACK_STRING;
        }

        if (Boolean.TRUE.equals(booleanValueObject))
            return "✅";

        return "❌";
    }

    @NonNull
    public static String formatToYesOrNo(@Nullable Object booleanValueObject) {

        if (booleanValueObject == null) {
            return FALLBACK_STRING;
        }


        if (Boolean.TRUE.equals(booleanValueObject))
            return "Yes";

        return "No";
    }

    @NonNull
    public static String formatToEnabledOrDisabled(@Nullable Object booleanValueObject) {
        if (booleanValueObject == null) {
            return FALLBACK_STRING;
        }

        if (Boolean.TRUE.equals(booleanValueObject))
            return "Enabled";

        return "Disabled";
    }
}
