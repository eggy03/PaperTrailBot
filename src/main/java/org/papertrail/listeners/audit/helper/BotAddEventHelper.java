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
public class BotAddEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Bot Add Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        User target = ale.getJDA().getUserById(ale.getTargetIdLong());

        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
        String mentionableTarget = (target !=null ? target.getAsMention() : ale.getTargetId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ A bot was added");
        eb.setColor(Color.CYAN);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);
        eb.addField("🤖 Added a bot: ", "╰┈➤"+mentionableTarget, false);

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
