package io.github.eggy03.papertrail.bot.commons.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
@Slf4j
public class BooleanUtils {

    @NotNull
    public static String formatToEmoji(@Nullable Object booleanValueObject) {

        if (booleanValueObject == null) {
            log.debug("boolean value for formatting to emoji is null");
            return "N/A";
        }

        if (Boolean.TRUE.equals(booleanValueObject))
            return "✅";

        return "❌";
    }

    @NotNull
    public static String formatToYesOrNo(@Nullable Object booleanValueObject) {

        if (booleanValueObject == null) {
            log.debug("boolean value for formatting to yes/no is null");
            return "N/A";
        }


        if (Boolean.TRUE.equals(booleanValueObject))
            return "Yes";

        return "No";
    }

    @NotNull
    public static String formatToEnabledOrDisabled(@Nullable Object booleanValueObject) {
        if (booleanValueObject == null) {
            log.debug("boolean value for formatting to enabled/disabled is null");
            return "N/A";
        }

        if (Boolean.TRUE.equals(booleanValueObject))
            return "Enabled";

        return "Disabled";
    }
}
