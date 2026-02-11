package io.github.eggy03.papertrail.bot.listeners.audit.helper.scheduledevent;

import io.github.eggy03.papertrail.bot.listeners.audit.helper.scheduledevent.utils.ScheduledEventUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
@Slf4j
public class ScheduledEventUpdateEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Scheduled Event Update Event");
        eb.setDescription("ℹ️ The following scheduled event was updated by: "+mentionableExecutor);
        eb.setColor(Color.YELLOW);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "entity_type" -> {
                    eb.addField("Old Event Type", "╰┈➤"+ScheduledEventUtils.resolveEventType(oldValue), false);
                    eb.addField("New Event Type", "╰┈➤"+ScheduledEventUtils.resolveEventType(newValue), false);
                }
                case "name" -> {
                    eb.addField("Old Event Name", "╰┈➤"+oldValue, false);
                    eb.addField("New Event Name","╰┈➤"+newValue, false);
                }
                case "description" -> {
                    eb.addField("Old Event Description", "╰┈➤"+oldValue, false);
                    eb.addField("New Event Description", "╰┈➤"+newValue, false);
                }
                case "status" -> {
                    eb.addField("Old Event Status", "╰┈➤"+ScheduledEventUtils.resolveStatusType(oldValue), false);
                    eb.addField("New Event Status", "╰┈➤"+ScheduledEventUtils.resolveStatusType(newValue), false);
                }
                case "location" -> {
                    eb.addField("Event Location", "╰┈➤"+oldValue, false);
                    eb.addField("Event Location", "╰┈➤"+newValue, false);
                }
                case "privacy_level" -> eb.addField("Privacy", "╰┈➤Event privacy has been updated", false);
                case "image_hash" -> eb.addField("Image", "╰┈➤Event Image has been updated", false);
                case "channel_id" -> eb.addField("Channel", "╰┈➤Event Channel has been updated", false);

                case "recurrence_rule" -> {
                    eb.addField("Old Recurrence Rule", ScheduledEventUtils.resolveRecurrenceRules(oldValue), false);
                    eb.addField("New Recurrence Rule", ScheduledEventUtils.resolveRecurrenceRules(newValue), false);
                }
                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();
        if(!mb.isSendable()){
            log.warn("Embed is empty or too long (current length: {}).", eb.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
