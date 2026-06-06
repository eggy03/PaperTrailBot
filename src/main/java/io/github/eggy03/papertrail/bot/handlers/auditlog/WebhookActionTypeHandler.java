package io.github.eggy03.papertrail.bot.handlers.auditlog;

import io.github.eggy03.papertrail.bot.listeners.auditlog.GuildAuditLogEntryCreateEventActionTypeHandler;
import io.github.eggy03.papertrail.bot.utils.auditlog.GuildUtils;
import io.github.eggy03.papertrail.bot.utils.auditlog.WebhookUtils;
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
public final class WebhookActionTypeHandler extends GuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull AuditLogRegistrationClient client;

    @Inject
    public WebhookActionTypeHandler(@NonNull AuditLogRegistrationClient client) {
        this.client = client;
    }

    @NonNull
    private String getRegisteredChannelId(@NonNull String guildId) {
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
    public void onWebhookCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Webhook Create Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Webhook Created By: " + mentionableExecutor));
        eb.setColor(Color.GREEN);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "type" ->
                        eb.addField(MarkdownUtil.underline("Webhook Type"), "╰┈➤" + WebhookUtils.resolveWebhookEventType(newValue), false);
                case "avatar_hash" -> {
                    // skip
                }
                case "channel_id" ->
                        eb.addField(MarkdownUtil.underline("Channel"), "╰┈➤" + GuildUtils.resolveMentionableChannel(newValue, event), false);
                case "name" -> eb.addField(MarkdownUtil.underline("Webhook Name"), "╰┈➤" + newValue, false);
                case "application_id" -> eb.addField(MarkdownUtil.underline("Application ID"), "╰┈➤" + newValue, false);
                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Webhook Create: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    @Override
    public void onWebhookUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Webhook Update Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Webhook Updated By: " + mentionableExecutor));
        eb.setColor(Color.YELLOW);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "type" -> {
                    eb.addField(MarkdownUtil.underline("Old Webhook Type"), "╰┈➤" + WebhookUtils.resolveWebhookEventType(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Webhook Type"), "╰┈➤" + WebhookUtils.resolveWebhookEventType(newValue), true);
                    eb.addBlankField(true);
                }
                case "avatar_hash" ->
                        eb.addField(MarkdownUtil.underline("Avatar"), "╰┈➤Avatar has been updated", false);

                case "channel_id" -> {
                    eb.addField(MarkdownUtil.underline("Old Channel"), "╰┈➤" + GuildUtils.resolveMentionableChannel(oldValue, event), true);
                    eb.addField(MarkdownUtil.underline("New Channel"), "╰┈➤" + GuildUtils.resolveMentionableChannel(newValue, event), true);
                    eb.addBlankField(true);
                }
                case "name" -> {
                    eb.addField(MarkdownUtil.underline("Old Webhook Name"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Webhook Name"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }
                case "application_id" -> {
                    eb.addField(MarkdownUtil.underline("Old Application ID"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Application ID"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }
                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Webhook Update: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    @Override
    public void onWebhookDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Webhook Remove Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Webhook Removed By: " + mentionableExecutor));
        eb.setColor(Color.RED);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "type" ->
                        eb.addField(MarkdownUtil.underline("Webhook Type"), "╰┈➤" + WebhookUtils.resolveWebhookEventType(oldValue), false);
                case "avatar_hash" -> {
                    // skip
                }
                case "channel_id" ->
                        eb.addField(MarkdownUtil.underline("Channel"), "╰┈➤" + GuildUtils.resolveMentionableChannel(oldValue, event), false);
                case "name" -> eb.addField(MarkdownUtil.underline("Webhook Name"), "╰┈➤" + oldValue, false);
                case "application_id" -> eb.addField(MarkdownUtil.underline("Application ID"), "╰┈➤" + oldValue, false);
                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Webhook Remove: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }
}
