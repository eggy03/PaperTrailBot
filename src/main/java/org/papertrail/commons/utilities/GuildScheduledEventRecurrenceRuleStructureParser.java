package org.papertrail.commons.utilities;

import lombok.experimental.UtilityClass;

import java.util.Map;

/**
 * Parser for: <a href="https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-recurrence-rule-object-guild-scheduled-event-recurrence-rule-frequency">Recurrence Rule Structure</a>
 */
@UtilityClass
public class GuildScheduledEventRecurrenceRuleStructureParser {

    public static String parse(Object recurrenceRuleStructure) {

        StringBuilder stringBuilder = new StringBuilder();
        if(recurrenceRuleStructure instanceof Map<?,?> recurrenceRuleMap) {

            stringBuilder
                    .append("**_Start Time of Interval_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(String.valueOf(recurrenceRuleMap.get("start")))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_End Time of Interval_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(String.valueOf(recurrenceRuleMap.get("end")))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Frequency of Interval_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(String.valueOf(recurrenceRuleMap.get("frequency")))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Spacing between events_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(String.valueOf(recurrenceRuleMap.get("interval")))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Weekday Recurrence_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(String.valueOf(recurrenceRuleMap.get("by_weekday")))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Specific days within a weekday to recur on_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(String.valueOf(recurrenceRuleMap.get("by_n_weekday")))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Monthly Recurrence_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(String.valueOf(recurrenceRuleMap.get("by_month")))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Specific days within a month to recur on_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(String.valueOf(recurrenceRuleMap.get("by_month_day")))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Set of days within a year to recur on_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(String.valueOf(recurrenceRuleMap.get("by_year_day")))
                    .append(System.lineSeparator());

            stringBuilder
                    .append("**_Event to be repeated_**")
                    .append(System.lineSeparator())
                    .append("╰┈➤")
                    .append(String.valueOf(recurrenceRuleMap.get("count")));

        }
        return stringBuilder.toString();
    }
}
