package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.guild;

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
public class GuildProfileUpdateEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Guild Profile Update Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Guild Profile Updated By: " + mentionableExecutor + "\nTarget Guild: " + event.getGuild().getName()));
        eb.setColor(Color.PINK);

        ale.getChanges().keySet().forEach(key -> {
            switch (key) {
                case "traits" ->
                        eb.addField(MarkdownUtil.underline("Server Traits"), "╰┈➤Server Traits have been updated", false);
                case "visibility" ->
                        eb.addField(MarkdownUtil.underline("Visibility"), "╰┈➤Profile Visibility has been changed", false);
                case "brand_color_primary" ->
                        eb.addField(MarkdownUtil.underline("Banner Color"), "╰┈➤Banner Color has been updated", false);
                case "game_application_ids" ->
                        eb.addField(MarkdownUtil.underline("Games"), "╰┈➤Games have been updated", false);
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
