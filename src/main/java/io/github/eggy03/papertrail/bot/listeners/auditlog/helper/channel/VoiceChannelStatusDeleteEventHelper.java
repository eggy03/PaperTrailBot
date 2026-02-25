package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.channel;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;

@UtilityClass
@Slf4j
public class VoiceChannelStatusDeleteEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        GuildChannel targetChannel = ale.getJDA().getGuildChannelById(ale.getTargetId());
        String mentionableTargetChannel = (targetChannel != null ? targetChannel.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Voice Channel Status Delete");

        eb.setDescription(MarkdownUtil.quoteBlock("A voice channel's status was deleted"));
        eb.setColor(Color.ORANGE);

        eb.addField(MarkdownUtil.underline("Status Deleted By"), "╰┈➤" + mentionableExecutor, false);
        eb.addField(MarkdownUtil.underline("Target Voice Channel"), "╰┈➤" + mentionableTargetChannel, false);

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
