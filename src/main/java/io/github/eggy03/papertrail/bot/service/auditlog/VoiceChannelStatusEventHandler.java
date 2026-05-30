package io.github.eggy03.papertrail.bot.service.auditlog;

import io.github.eggy03.papertrail.bot.listeners.auditlog.GuildAuditLogEntryCreateEventHandler;
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
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;

@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public class VoiceChannelStatusEventHandler extends GuildAuditLogEntryCreateEventHandler {

    private final @NonNull AuditLogRegistrationClient client;

    @Inject
    public VoiceChannelStatusEventHandler(@NonNull AuditLogRegistrationClient client) {
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
    public void onVoiceChannelStatusUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredGuildChannel(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        GuildChannel targetChannel = ale.getJDA().getGuildChannelById(ale.getTargetId());
        String mentionableTargetChannel = (targetChannel != null ? targetChannel.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Voice Channel Status Update");

        eb.setDescription("A voice channel status has been updated");
        eb.setColor(Color.YELLOW);

        eb.addField(
                MarkdownUtil.underline("Details"),
                MarkdownUtil.quoteBlock(
                        "Status Updated By: " + mentionableExecutor + "\n" +
                                "Target Channel: " + mentionableTargetChannel + "\n" +
                                "Updated Status: " + ale.getOptionByName("status")
                ),
                false
        );

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    @Override
    public void onVoiceChannelStatusDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredGuildChannel(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        GuildChannel targetChannel = ale.getJDA().getGuildChannelById(ale.getTargetId());
        String mentionableTargetChannel = (targetChannel != null ? targetChannel.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Voice Channel Status Delete");

        eb.setDescription("A voice channel status has been reset");
        eb.setColor(Color.ORANGE);

        // status deletes dont contain the deleted status content
        eb.addField(
                MarkdownUtil.underline("Details"),
                MarkdownUtil.quoteBlock("Status Reset By: " + mentionableExecutor + "\n" + "Target Channel: " + mentionableTargetChannel),
                false
        );

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }
}
