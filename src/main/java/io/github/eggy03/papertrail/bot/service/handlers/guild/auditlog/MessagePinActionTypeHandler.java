package io.github.eggy03.papertrail.bot.service.handlers.guild.auditlog;

import io.github.eggy03.papertrail.bot.environment.PaperTrailEnvironment;
import io.github.eggy03.papertrail.bot.service.EmbedCheckingService;
import io.github.eggy03.papertrail.bot.utils.auditlog.MessageUtils;
import io.github.eggy03.papertrail.http.client.PaperTrailGuildClient;
import io.github.eggy03.papertrail.http.entity.PaperTrailGuild;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class MessagePinActionTypeHandler extends AbstractGuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull PaperTrailGuildClient client;
    private final @NonNull PaperTrailEnvironment environment;
    private final @NonNull EmbedCheckingService embedCheckingService;

    @Inject
    public MessagePinActionTypeHandler(@NonNull PaperTrailGuildClient client, @NonNull PaperTrailEnvironment environment, @NonNull EmbedCheckingService embedCheckingService) {
        this.client = client;
        this.environment = environment;
        this.embedCheckingService = embedCheckingService;
    }

    @Nullable
    private String getRegisteredChannelId(@NonNull String guildId) {
        return client.getGuild(guildId)
                .map(PaperTrailGuild::guildEventChannelId)
                .orElse(null);

    }


    @Override
    public void onMessagePin(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo == null) return;

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Message Pin Event");

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        User target = ale.getJDA().getUserById(ale.getTargetId());
        String mentionableTarget = (target != null ? target.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Message Pinned By: " + mentionableExecutor + "\nMessage Author: " + mentionableTarget));
        eb.setColor(environment.embedColor().successColor());

        eb.addField(
                MarkdownUtil.underline("Pinned Message Jump URL"),
                MessageUtils.resolveMessageJumpUrlFromId(ale.getOptionByName("channel_id"), ale.getOptionByName("message_id"), event),
                false
        );

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }

    @Override
    public void onMessageUnpin(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo == null) return;

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Message Unpin Event");

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        User target = ale.getJDA().getUserById(ale.getTargetId());
        String mentionableTarget = (target != null ? target.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Message Un-Pinned By: " + mentionableExecutor + "\nMessage Author: " + mentionableTarget));
        eb.setColor(environment.embedColor().warningColor());

        eb.addField(
                MarkdownUtil.underline("Un-Pinned Message Jump URL"),
                MessageUtils.resolveMessageJumpUrlFromId(ale.getOptionByName("channel_id"), ale.getOptionByName("message_id"), event),
                false
        );

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }
}
