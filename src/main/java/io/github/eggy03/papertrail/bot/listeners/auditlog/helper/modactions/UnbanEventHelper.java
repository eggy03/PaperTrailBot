package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.modactions;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
@Slf4j
public class UnbanEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User moderator = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableModerator = (moderator != null ? moderator.getAsMention() : ale.getUserId());

        event.getJDA().retrieveUserById(ale.getTargetId()).queue(unbannedUser -> {

            String mentionableUnbannedUser = unbannedUser != null ? unbannedUser.getAsMention() : ale.getTargetId();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Member Unban Event");
            eb.setDescription("ℹ️ The following user was un-banned by: " + mentionableModerator);
            eb.setColor(Color.GREEN);

            eb.addField("Action Type", String.valueOf(ale.getType()), true);
            eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

            eb.addField("Un-banned User", "╰┈➤" + mentionableUnbannedUser, false);

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
