package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.automod;

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
public class AutoModerationRuleBlockMessageEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Auto-mod Event");

        User targetUser = ale.getJDA().getUserById(ale.getTargetId());
        String targetName = (targetUser != null ? targetUser.getEffectiveName() : ale.getTargetId());

        eb.setDescription("AutoMod has blocked a message");
        eb.setColor(Color.ORANGE);

        eb.addField(
                MarkdownUtil.underline("Target Member"),
                MarkdownUtil.codeblock(targetName),
                false
        );

        eb.addField(
                MarkdownUtil.underline("Additional Info"),
                MarkdownUtil.codeblock("Blocked message will be available in the channel set to receive AutoMod events."),
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
