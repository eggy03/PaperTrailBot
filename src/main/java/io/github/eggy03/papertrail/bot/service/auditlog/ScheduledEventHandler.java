package io.github.eggy03.papertrail.bot.service.auditlog;

import io.github.eggy03.papertrail.bot.listeners.auditlog.GuildAuditLogEntryCreateEventHandler;
import io.github.eggy03.papertrail.bot.utils.auditlog.ScheduledEventUtils;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;

@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class ScheduledEventHandler extends GuildAuditLogEntryCreateEventHandler {

    private final @NonNull AuditLogRegistrationClient client;

    @Inject
    public ScheduledEventHandler(@NonNull AuditLogRegistrationClient client) {
        this.client = client;
    }

    @NonNull
    private String getRegisteredGuildChannel(@NonNull String guildId) {
        return client.getRegisteredGuild(guildId)
                .map(AuditLogRegistrationEntity::getChannelId).orElse(StringUtils.EMPTY);

    }

    private void performChecksThenBuildAndSendEmbed(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull EmbedBuilder embedBuilder, @NonNull String channelIdToSendTo) {
        if (!embedBuilder.isValidLength() || embedBuilder.isEmpty()) {
            log.warn("Embed is empty or too long (current length: {}).", embedBuilder.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    @Override
    public void onScheduledEventCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredGuildChannel(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Scheduled Event Create Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Scheduled Event Created By: " + mentionableExecutor));
        eb.setColor(Color.GREEN);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "entity_type" ->
                        eb.addField(MarkdownUtil.underline("Event Type"), "╰┈➤" + ScheduledEventUtils.resolveEventType(newValue), false);
                case "name" -> eb.addField(MarkdownUtil.underline("Event Name"), "╰┈➤" + newValue, false);
                case "description" -> eb.addField(MarkdownUtil.underline("Event Description"), "╰┈➤" + newValue, false);
                case "status" ->
                        eb.addField(MarkdownUtil.underline("Event Status"), "╰┈➤" + ScheduledEventUtils.resolveStatusType(newValue), false);
                case "location" -> eb.addField(MarkdownUtil.underline("Event Location"), "╰┈➤" + newValue, false);
                case "privacy_level", "image_hash", "channel_id", "recurrence_rule" -> {
                    // skip
                }
                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Scheduled Event Create: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());


        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    @Override
    public void onScheduledEventUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredGuildChannel(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

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
                    log.info("Unimplemented Change Key on Scheduled Event Update: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());


        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    @Override
    public void onScheduledEventDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredGuildChannel(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Scheduled Event Delete Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Scheduled Event Deleted By: " + mentionableExecutor));
        eb.setColor(Color.RED);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "entity_type" ->
                        eb.addField(MarkdownUtil.underline("Event Type"), "╰┈➤" + ScheduledEventUtils.resolveEventType(oldValue), false);
                case "name" -> eb.addField(MarkdownUtil.underline("Event Name"), "╰┈➤" + oldValue, false);
                case "description" -> eb.addField(MarkdownUtil.underline("Event Description"), "╰┈➤" + oldValue, false);
                case "status" ->
                        eb.addField(MarkdownUtil.underline("Event Status"), "╰┈➤" + ScheduledEventUtils.resolveStatusType(oldValue), false);
                case "location" -> eb.addField(MarkdownUtil.underline("Event Location"), "╰┈➤" + oldValue, false);
                case "privacy_level", "image_hash", "channel_id", "recurrence_rule" -> {
                    // skip
                }
                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Scheduled Event Delete: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }
}
