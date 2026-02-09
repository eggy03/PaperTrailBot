package io.github.eggy03.papertrail.bot.commons.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class BooleanUtils {

    @NotNull
    public static String formatToEmoji (Object value) {
        if(value==null)
            return "N/A";

        if(Boolean.TRUE.equals(value))
            return "✅";

        return "❌";
    }

    @NotNull
    public static String formatToYesOrNo (Object value) {
        if(value==null)
            return "N/A";

        if(Boolean.TRUE.equals(value))
            return "Yes";

        return "No";
    }

    @NotNull
    public static String formatToEnabledOrDisabled (Object value) {
        if(value==null)
            return "N/A";

        if(Boolean.TRUE.equals(value))
            return "Enabled";

        return "Disabled";
    }
}
