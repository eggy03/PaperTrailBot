package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.homesettings;

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
public class HomeSettingsUpdateEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Server Guide Update Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Server Guide Updated By: " + mentionableExecutor));
        eb.setColor(Color.YELLOW);

        ale.getChanges().keySet().forEach(key -> {
            switch (key) {
                case "welcome_message" ->
                        eb.addField(MarkdownUtil.underline("Welcome Message"), "╰┈➤Welcome Message has been updated", false);
                case "resource_channels" ->
                        eb.addField(MarkdownUtil.underline("Resources"), "╰┈➤Resources have been updated", false);
                case "new_member_actions" ->
                        eb.addField(MarkdownUtil.underline("New Member Join To-Do"), "╰┈➤Interactive Actions have been updated", false);
                default -> eb.addField(MarkdownUtil.underline(key), "╰┈➤" + key + " has/have been updated", false);
            }
        });

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
