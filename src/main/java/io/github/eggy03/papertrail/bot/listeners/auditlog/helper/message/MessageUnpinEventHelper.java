package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.message;

import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.message.utils.MessageUtils;
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
public class MessageUnpinEventHelper {
    
    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Message Unpin Event");

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        User target = ale.getJDA().getUserById(ale.getTargetId());
        String mentionableTarget = (target != null ? target.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Message Un-Pinned By: " + mentionableExecutor + "\nMessage Author: " + mentionableTarget));
        eb.setColor(Color.MAGENTA);

        eb.addField(
                MarkdownUtil.underline("Un-Pinned Message Jump URL"),
                MessageUtils.resolveMessageJumpUrlFromId(ale.getOptionByName("channel_id"), ale.getOptionByName("message_id"), event),
                false
        );

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
