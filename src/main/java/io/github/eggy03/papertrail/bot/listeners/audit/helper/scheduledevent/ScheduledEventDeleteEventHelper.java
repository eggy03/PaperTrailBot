package io.github.eggy03.papertrail.bot.listeners.audit.helper.scheduledevent;

import io.github.eggy03.papertrail.bot.commons.utilities.GuildScheduledEventRecurrenceRuleStructureParser;
import io.github.eggy03.papertrail.bot.commons.utilities.TypeResolver;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;
import java.util.Map;

@UtilityClass
@Slf4j
public class ScheduledEventDeleteEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Scheduled Event Delete Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ A scheduled event has been deleted");
        eb.setColor(Color.RED);
        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {
                case "entity_type":
                    eb.addField("📂 Event Type", "╰┈➤"+ TypeResolver.scheduleEventTypeResolver(oldValue), false);
                    break;

                case "privacy_level", "image_hash":
                    break;

                case "name":
                    eb.addField("🏷️ Event Name", "╰┈➤"+oldValue, false);
                    break;

                case "description":
                    eb.addField("📝 Event Description", "╰┈➤"+newValue, false);
                    break;

                case "status":
                    eb.addField("📊 Event Status", "╰┈➤"+TypeResolver.scheduleEventStatusTypeResolver(oldValue), false);
                    break;

                case "location":
                    eb.addField("📍 Event Location", "╰┈➤"+oldValue, false);
                    break;

                case "channel_id":
                    GuildChannel eventChannel = event.getGuild().getGuildChannelById(String.valueOf(oldValue));
                    eb.addField("💬 Event Channel", "╰┈➤"+(eventChannel!=null ? eventChannel.getAsMention() : String.valueOf(oldValue)), false);
                    break;

                case "recurrence_rule":
                    eb.addField("📊 Recurrence Rule", GuildScheduledEventRecurrenceRuleStructureParser.parse(oldValue), false);
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
