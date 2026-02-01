package org.papertrail.commons.utilities;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class BooleanFormatter {

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
}
