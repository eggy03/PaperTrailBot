package io.github.eggy03.papertrail.bot.utils.auditlog;

import io.github.eggy03.papertrail.bot.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.jetbrains.annotations.Nullable;

@UtilityClass
@Slf4j
public final class ScheduledEventUtils {

    @NonNull
    public static final String FALLBACK_STRING = "N/A";

    @NonNull
    public static String resolveEventType(@Nullable Object eventTypeInteger) {

        Integer eventType = NumberParseUtils.parseInt(eventTypeInteger);
        if (eventType == null)
            return FALLBACK_STRING;

        return ScheduledEvent.Type.fromKey(eventType).name();

    }

    @NonNull
    public static String resolveStatusType(@Nullable Object eventTypeInteger) {
        Integer eventType = NumberParseUtils.parseInt(eventTypeInteger);
        if (eventType == null)
            return FALLBACK_STRING;

        return ScheduledEvent.Status.fromKey(eventType).name();
    }
}
