package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.scheduledevent;

import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.scheduledevent.utils.ScheduledEventUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

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
        eb.setDescription(MarkdownUtil.quoteBlock("Scheduled Event Updated By: " + mentionableExecutor));
        eb.setColor(Color.YELLOW);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "entity_type" -> {
                    eb.addField(MarkdownUtil.underline("Old Event Type"), "╰┈➤" + ScheduledEventUtils.resolveEventType(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Event Type"), "╰┈➤" + ScheduledEventUtils.resolveEventType(newValue), true);
                    eb.addBlankField(true);
                }
                case "name" -> {
                    eb.addField(MarkdownUtil.underline("Old Event Name"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Event Name"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }
                case "description" -> {
                    eb.addField(MarkdownUtil.underline("Old Event Description"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Event Description"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }
                case "status" -> {
                    eb.addField(MarkdownUtil.underline("Old Event Status"), "╰┈➤" + ScheduledEventUtils.resolveStatusType(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Event Status"), "╰┈➤" + ScheduledEventUtils.resolveStatusType(newValue), true);
                    eb.addBlankField(true);
                }
                case "location" -> {
                    eb.addField(MarkdownUtil.underline("Event Location"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("Event Location"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }
                case "privacy_level" ->
                        eb.addField(MarkdownUtil.underline("Privacy"), "╰┈➤Event privacy has been updated", false);
                case "image_hash" ->
                        eb.addField(MarkdownUtil.underline("Image"), "╰┈➤Event Image has been updated", false);
                case "channel_id" ->
                        eb.addField(MarkdownUtil.underline("Channel"), "╰┈➤Event Channel has been updated", false);
                case "recurrence_rule" ->
                        eb.addField(MarkdownUtil.underline("Recurrence Rule"), "╰┈➤Recurrence Rule has been updated", false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        if (!eb.isValidLength() || eb.isEmpty()) {
            log.warn("Embed is empty or too long (current length: {}).", eb.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }
}
