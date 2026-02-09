package io.github.eggy03.papertrail.bot.listeners.audit.helper.scheduledevent;

import io.github.eggy03.papertrail.bot.commons.utils.GuildScheduledEventRecurrenceRuleStructureParser;
import io.github.eggy03.papertrail.bot.commons.utils.TypeResolver;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;
import java.util.Map;
import java.util.Objects;

@UtilityClass
@Slf4j
public class ScheduledEventUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Scheduled Event Update Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        ScheduledEvent targetEvent = event.getGuild().getScheduledEventById(ale.getTargetId());
        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following scheduled event was updated: "+ Objects.requireNonNull(targetEvent).getName());
        eb.setColor(Color.YELLOW);
        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {
                case "entity_type":
                    eb.addField("📂 Old Event Type", "╰┈➤"+ TypeResolver.scheduleEventTypeResolver(oldValue), false);
                    eb.addField("📂 New Event Type", "╰┈➤"+TypeResolver.scheduleEventTypeResolver(newValue), false);
                    break;

                case "privacy_level", "image_hash":
                    break;

                case "name":
                    eb.addField("🏷️ Old Event Name", "╰┈➤"+oldValue, false);
                    eb.addField("🏷️ New Event Name","╰┈➤"+newValue, false);
                    break;

                case "description":
                    eb.addField("📝 Old Event Description", "╰┈➤"+oldValue, false);
                    eb.addField("📝 New Event Description", "╰┈➤"+newValue, false);
                    break;

                case "status":
                    eb.addField("📊 Old Event Status", "╰┈➤"+TypeResolver.scheduleEventStatusTypeResolver(oldValue), false);
                    eb.addField("📊 New Event Status", "╰┈➤"+TypeResolver.scheduleEventStatusTypeResolver(newValue), false);
                    break;

                case "location":
                    eb.addField("📍 Event Location", "╰┈➤"+oldValue, false);
                    eb.addField("📍 Event Location", "╰┈➤"+newValue, false);
                    break;

                case "channel_id":
                    GuildChannel eventChannel = event.getGuild().getGuildChannelById(String.valueOf(oldValue));
                    eb.addField("💬 Old Event Channel", "╰┈➤"+(eventChannel!=null ? eventChannel.getAsMention() : String.valueOf(oldValue)), false);
                    eventChannel = event.getGuild().getGuildChannelById(String.valueOf(newValue));
                    eb.addField("💬 New Event Channel", "╰┈➤"+(eventChannel!=null ? eventChannel.getAsMention() : String.valueOf(newValue)), false);
                    break;

                case "recurrence_rule":
                    eb.addField("📊 Old Recurrence Rule", GuildScheduledEventRecurrenceRuleStructureParser.parse(oldValue), false);
                    eb.addField("📊 New Recurrence Rule", GuildScheduledEventRecurrenceRuleStructureParser.parse(newValue), false);
                    break;

                default:
                    eb.addField(change, "from "+oldValue+" to "+newValue, false);
            }

        }

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();
        if(!mb.isSendable()){
            log.warn("An embed is either empty or has exceed the max length for characters, with current length: {}", eb.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
