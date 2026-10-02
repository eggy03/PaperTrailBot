package io.github.eggy03.papertrail.bot.service.handlers.guild.auditlog;

import io.github.eggy03.papertrail.bot.service.EmbedCheckingService;
import io.github.eggy03.papertrail.http.client.PaperTrailGuildClient;
import io.github.eggy03.papertrail.http.entity.PaperTrailGuild;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jspecify.annotations.Nullable;

import java.awt.Color;

@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class VoiceChannelStatusActionTypeHandler extends AbstractGuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull PaperTrailGuildClient client;
    private final @NonNull EmbedCheckingService embedCheckingService;

    @Inject
    public VoiceChannelStatusActionTypeHandler(@NonNull PaperTrailGuildClient client, @NonNull EmbedCheckingService embedCheckingService) {
        this.client = client;
        this.embedCheckingService = embedCheckingService;
    }

    @Nullable
    private String getRegisteredChannelId(@NonNull String guildId) {
        return client.getGuild(guildId)
                .map(PaperTrailGuild::guildEventChannelId)
                .orElse(null);

    }


    @Override
    public void onVoiceChannelStatusUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo == null) return;

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

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }

    @Override
    public void onVoiceChannelStatusDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo == null) return;

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

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }
}
