package io.github.eggy03.papertrail.bot.listeners.audit.helper.automod.utils;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.automod.AutoModEventType;
import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.eggy03.papertrail.bot.commons.utils.NumberFormatUtils.parseInt;

@UtilityClass
public class AutoModUtils {

    public static final String NOT_AVAILABLE = "N/A";

    // AUTO MOD UTILS
    @NotNull
    public static String autoModEventTypeResolver(@Nullable Object autoModEventTypeInteger) {
        Integer eventTypeInt = parseInt(autoModEventTypeInteger);
        if (eventTypeInt == null) {
            return NOT_AVAILABLE;
        }

        return AutoModEventType.fromKey(eventTypeInt).name();
    }

    public static String autoModTriggerTypeResolver(@Nullable Object autoModTriggerTypeInteger) {
        Integer triggerTypeInt = parseInt(autoModTriggerTypeInteger);
        if (triggerTypeInt == null)
            return NOT_AVAILABLE;


       return AutoModTriggerType.fromKey(triggerTypeInt).name();
    }
}
