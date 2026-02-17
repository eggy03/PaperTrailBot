package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.automod.utils;

import io.github.eggy03.papertrail.bot.commons.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.automod.AutoModEventType;
import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/*
 * See {@link io.github.eggy03.papertrail.bot.commons.utils.StackWalkerUtils}
 * and other utilities in the util package
 * to know why logging isn't needed here
 */
@UtilityClass
public class AutoModUtils {

    @NonNull
    public static final String FALLBACK_STRING = "N/A";

    @NotNull
    public static String autoModEventTypeResolver(@Nullable Object autoModEventTypeInteger) {
        Integer eventTypeInt = NumberParseUtils.parseInt(autoModEventTypeInteger);
        if (eventTypeInt == null) {
            return FALLBACK_STRING;
        }

        return AutoModEventType.fromKey(eventTypeInt).name();
    }

    @NotNull
    public static String autoModTriggerTypeResolver(@Nullable Object autoModTriggerTypeInteger) {
        Integer triggerTypeInt = NumberParseUtils.parseInt(autoModTriggerTypeInteger);
        if (triggerTypeInt == null)
            return FALLBACK_STRING;


        return AutoModTriggerType.fromKey(triggerTypeInt).name();
    }
}
