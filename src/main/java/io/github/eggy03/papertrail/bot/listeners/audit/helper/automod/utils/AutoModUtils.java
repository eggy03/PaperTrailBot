package io.github.eggy03.papertrail.bot.listeners.audit.helper.automod.utils;

import io.github.eggy03.papertrail.bot.commons.utils.NumberParseUtils;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.automod.AutoModEventType;
import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class AutoModUtils {

    public static final String NOT_AVAILABLE = "N/A";

    // AUTO MOD UTILS
    @NotNull
    public static String autoModEventTypeResolver(@Nullable Object autoModEventTypeInteger) {
        Integer eventTypeInt = NumberParseUtils.parseInt(autoModEventTypeInteger);
        if (eventTypeInt == null) {
            return NOT_AVAILABLE;
        }

        return AutoModEventType.fromKey(eventTypeInt).name();
    }

    public static String autoModTriggerTypeResolver(@Nullable Object autoModTriggerTypeInteger) {
        Integer triggerTypeInt = NumberParseUtils.parseInt(autoModTriggerTypeInteger);
        if (triggerTypeInt == null)
            return NOT_AVAILABLE;


       return AutoModTriggerType.fromKey(triggerTypeInt).name();
    }
}
