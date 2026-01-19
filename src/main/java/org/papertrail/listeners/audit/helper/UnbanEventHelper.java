package org.papertrail.listeners.audit.helper;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
public class UnbanEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Member Unban Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription("👤 **By**: " + mentionableExecutor + "\nℹ️ The following user was un-banned");
        eb.setColor(Color.GREEN);
        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        String moderatorId = ale.getUserId();
        String targetId = ale.getTargetId();

        event.getJDA().retrieveUserById(moderatorId).queue(moderator ->
                event.getJDA().retrieveUserById(targetId).queue(target -> {
                    // if user objects are null we cannot use their mention so we instead use their IDs instead since they will never be null
                    eb.addField("🔓 A member has been un-banned", "╰┈➤" + (moderator != null ? moderator.getAsMention() : moderatorId) + " has un-banned " + (target != null ? target.getAsMention() : targetId), false);

                    eb.setFooter("Audit Log Entry ID: " + ale.getId());
                    eb.setTimestamp(ale.getTimeCreated());
                    MessageEmbed mb = eb.build();
                    TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
                    if (sendingChannel != null && sendingChannel.canTalk()) {
                        sendingChannel.sendMessageEmbeds(mb).queue();
                    }
                })
        );
    }
}
