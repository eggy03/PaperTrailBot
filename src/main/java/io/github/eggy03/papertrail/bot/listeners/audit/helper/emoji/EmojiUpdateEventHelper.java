package io.github.eggy03.papertrail.bot.listeners.audit.helper.emoji;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
public class EmojiUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());


        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Emoji Update Event");
        eb.setDescription("ℹ️ The following emoji was updated by: "+mentionableExecutor);
        eb.setColor(Color.YELLOW);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            if (changeKey.equals("name")) {
                eb.addField("Emoji Name Updated", "╰┈➤"+"From "+oldValue+" to "+newValue, false);
                eb.addField("Target Emoji", "╰┈➤"+"<:"+newValue+":"+ale.getTargetId()+">", false);
            } else {
                eb.addField(changeKey, "OLD_VALUE: " + oldValue, false);
                eb.addField(changeKey, "NEW_VALUE: " + newValue, false);
            }
        });

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }
}
