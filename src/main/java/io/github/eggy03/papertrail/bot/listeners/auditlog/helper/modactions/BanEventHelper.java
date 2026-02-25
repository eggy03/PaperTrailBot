package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.modactions;

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
public class BanEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

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
}
