package io.github.eggy03.papertrail.bot.service.auditlog;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;

@ApplicationScoped
@Slf4j
public class ModActionEventHandlerService {

    public void handleBanEvent(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
        AuditLogEntry ale = event.getEntry();

        User moderator = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableModerator = (moderator != null ? moderator.getAsMention() : ale.getUserId());
        String reasonForBan = ale.getReason() == null ? "No Reason Provided" : ale.getReason();

        // A REST Action is required here because banned members are not cached
        event.getJDA().retrieveUserById(ale.getTargetId()).queue(bannedUser -> {

            String mentionableBannedUser = bannedUser != null ? bannedUser.getAsMention() : ale.getTargetId();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Ban Event");
            eb.setDescription(MarkdownUtil.quoteBlock("A Member Has Been Banned By: " + mentionableModerator));
            eb.setColor(Color.RED);

            eb.addField(MarkdownUtil.underline("Banned Member"), "╰┈➤" + mentionableBannedUser, false);
            eb.addField(MarkdownUtil.underline("Ban Reason"), "╰┈➤" + reasonForBan, false);

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
        });
    }

    public void handleKickEvent(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
        AuditLogEntry ale = event.getEntry();

        User moderator = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableModerator = (moderator != null ? moderator.getAsMention() : ale.getUserId());
        String reasonForKick = ale.getReason() == null ? "No Reason Provided" : ale.getReason();

        // A REST Action is required here because kicked members are not cached
        event.getJDA().retrieveUserById(ale.getTargetId()).queue(kickedUser -> {

            String mentionableKickedUser = kickedUser != null ? kickedUser.getAsMention() : ale.getTargetId();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Kick Event");
            eb.setDescription(MarkdownUtil.quoteBlock("A Member Has Been Kicked By: " + mentionableModerator));
            eb.setColor(Color.ORANGE);

            eb.addField(MarkdownUtil.underline("Kicked Member"), "╰┈➤" + mentionableKickedUser, false);
            eb.addField(MarkdownUtil.underline("Reason"), "╰┈➤" + reasonForKick, false);

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
        });
    }

    public void handleUnbanEvent(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
        AuditLogEntry ale = event.getEntry();

        User moderator = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableModerator = (moderator != null ? moderator.getAsMention() : ale.getUserId());

        event.getJDA().retrieveUserById(ale.getTargetId()).queue(unbannedUser -> {

            String mentionableUnbannedUser = unbannedUser != null ? unbannedUser.getAsMention() : ale.getTargetId();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Member Unban Event");
            eb.setDescription(MarkdownUtil.quoteBlock("A Member Has Been Un-Banned By: " + mentionableModerator));
            eb.setColor(Color.GREEN);

            eb.addField(MarkdownUtil.underline("Un-banned User"), "╰┈➤" + mentionableUnbannedUser, false);

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
        });
    }

    public void handleBotAddEvent(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        User target = ale.getJDA().getUserById(ale.getTargetIdLong());
        String mentionableTarget = (target != null ? target.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Bot Add Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Bot Added By: " + mentionableExecutor + "\nBot ID: " + ale.getTargetId()));
        eb.setColor(Color.CYAN);

        eb.addField(MarkdownUtil.underline("Bot Added"), "╰┈➤" + mentionableTarget, false);

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
