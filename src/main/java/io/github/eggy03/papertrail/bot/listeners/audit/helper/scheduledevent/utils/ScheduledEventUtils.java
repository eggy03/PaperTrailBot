package io.github.eggy03.papertrail.bot.listeners.audit.helper.scheduledevent.utils;

import io.github.eggy03.papertrail.bot.commons.utils.NumberParseUtils;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@UtilityClass
public class ScheduledEventUtils {

    @NotNull
    public static String resolveEventType (@Nullable Object eventTypeInteger) {

        Integer eventType = NumberParseUtils.parseInt(eventTypeInteger);
        if (eventType == null)
            return "N/A";

        return ScheduledEvent.Type.fromKey(eventType).name();

    }

    @NotNull
    public static String resolveStatusType (@Nullable Object eventTypeInteger) {
        Integer eventType = NumberParseUtils.parseInt(eventTypeInteger);
        if (eventType == null)
            return "N/A";

        return ScheduledEvent.Status.fromKey(eventType).name();
    }

    @NotNull
    public static String resolveRecurrenceRules(@Nullable Object recurrenceRuleStructure) {

        if(recurrenceRuleStructure==null)
            return "N/A";

        StringBuilder stringBuilder = new StringBuilder();
        if(recurrenceRuleStructure instanceof Map<?,?> recurrenceRuleMap) {

            stringBuilder
                    .append("**_Start Time of Interval_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(recurrenceRuleMap.get("start"))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_End Time of Interval_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(recurrenceRuleMap.get("end"))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Frequency of Interval_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(recurrenceRuleMap.get("frequency"))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Spacing between events_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(recurrenceRuleMap.get("interval"))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Weekday Recurrence_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(recurrenceRuleMap.get("by_weekday"))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Specific days within a weekday to recur on_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(recurrenceRuleMap.get("by_n_weekday"))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Monthly Recurrence_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(recurrenceRuleMap.get("by_month"))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Specific days within a month to recur on_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(recurrenceRuleMap.get("by_month_day"))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Set of days within a year to recur on_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(recurrenceRuleMap.get("by_year_day"))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Event to be repeated_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(recurrenceRuleMap.get("count"));

        }
        return stringBuilder.toString().trim();
    }
}
