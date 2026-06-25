package io.github.eggy03.papertrail.bot.utils.auditlog;

import io.github.eggy03.papertrail.bot.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;

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

    // parse ISO date time format to Discord style
    @NonNull
    public static String convertISOTimeToDiscordTimeStamp(@Nullable Object isoDateTime) {
        if (isoDateTime == null) return FALLBACK_STRING;
        return TimeFormat.DATE_TIME_LONG.format(OffsetDateTime.parse(isoDateTime.toString()));
    }
}
