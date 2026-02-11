package io.github.eggy03.papertrail.bot.commons.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class BooleanUtils {

    @NotNull
    public static String formatToEmoji (@Nullable Object value) {
        if(value==null)
            return "N/A";

        if(Boolean.TRUE.equals(value))
            return "✅";

        return "❌";
    }

    @NotNull
    public static String formatToYesOrNo (@Nullable Object value) {
        if(value==null)
            return "N/A";

        if(Boolean.TRUE.equals(value))
            return "Yes";

        return "No";
    }

    @NotNull
    public static String formatToEnabledOrDisabled (@Nullable Object value) {
        if(value==null)
            return "N/A";

        if(Boolean.TRUE.equals(value))
            return "Enabled";

        return "Disabled";
    }
}
