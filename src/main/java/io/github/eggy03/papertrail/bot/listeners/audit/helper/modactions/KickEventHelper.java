package io.github.eggy03.papertrail.bot.listeners.audit.helper.modactions;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
public class KickEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User moderator = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableModerator = (moderator != null ? moderator.getAsMention() : ale.getUserId());
        String reasonForKick = ale.getReason()==null ? "No Reason Provided" : ale.getReason();

        // A REST Action is required here because kicked members are not cached
        event.getJDA().retrieveUserById(ale.getTargetId()).queue(kickedUser -> {

            String mentionableKickedUser = kickedUser!=null ? kickedUser.getAsMention() : ale.getTargetId();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Kick Event");
            eb.setDescription("ℹ️ The following member was kicked by: "+mentionableModerator);
            eb.setColor(Color.ORANGE);

            eb.addField("Action Type", String.valueOf(ale.getType()), true);
            eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

            eb.addField("Kicked Member/Application", "╰┈➤" + mentionableKickedUser, false);
            eb.addField("Reason", "╰┈➤" + reasonForKick, false);

            eb.setFooter("Audit Log Entry ID: " + ale.getId());
            eb.setTimestamp(ale.getTimeCreated());

            TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
            if (sendingChannel != null && sendingChannel.canTalk()) {
                sendingChannel.sendMessageEmbeds(eb.build()).queue();
            }
        });
    }
}
