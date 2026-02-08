package io.github.eggy03.papertrail.bot.listeners.audit.helper.automod.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class AutomodUtil {

    @NotNull
    public static String automodEventTypeResolver(@Nullable Object type) {
        if (type == null) {
            return "Null Automod Event Type";
        }

        try {
            int typeInt = Integer.parseInt(type.toString());
            return switch (typeInt) {
                case 1 -> "Message Send";
                case 2 -> "Message Update";
                default -> "Unknown";
            };
        } catch (NumberFormatException e) {
            return "Type Cannot Be Parsed";
        }

    }

    public static String automodTriggerTypeResolver(@Nullable Object type) {
        if (type == null) {
            return "Null Automod Trigger Type";
        }

        try {
            int typeInt = Integer.parseInt(type.toString());
            return switch (typeInt) {
                case 1 -> "Keyword";
                case 3 -> "Spam";
                case 4 -> "Keyword Preset";
                case 5 -> "Mention Spam";
                case 6 -> "Member Profile";
                default -> "Unknown";
            };
        } catch (NumberFormatException e) {
            return "Type Cannot Be Parsed";
        }

    }
}
