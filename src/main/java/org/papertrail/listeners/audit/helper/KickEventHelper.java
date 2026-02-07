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
public class KickEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Kick Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription("👤 **By**: " + mentionableExecutor + "\nℹ️ The following member was kicked");
        eb.setColor(Color.ORANGE);
        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        String moderatorId = ale.getUserId();
        String targetId = ale.getTargetId();
        String reason = ale.getReason();

        // fixme -> find a way to not use REST Action
        // A REST Action is required here because kicked members are not cached
        event.getJDA().retrieveUserById(moderatorId).queue(moderator ->
                event.getJDA().retrieveUserById(targetId).queue(target -> {
                    // if user objects are null we cannot use their mention so we instead use their IDs instead since they will never be null
                    eb.addField("👢 A member/application has been kicked", "╰┈➤" + (moderator != null ? moderator.getAsMention() : moderatorId) + " has kicked " + (target != null ? target.getAsMention() : targetId), false);
                    eb.addField("📝 With Reason", "╰┈➤" + (reason != null ? reason : "No Reason Provided"), false);

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
