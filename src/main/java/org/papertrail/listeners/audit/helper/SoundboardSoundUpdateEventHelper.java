package org.papertrail.listeners.audit.helper;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;
import java.util.Map;

@UtilityClass
public class SoundboardSoundUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Soundboard Sound Update Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ A sound item in the soundboard was updated");
        eb.setColor(Color.YELLOW);
        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {

                case "user_id", "sound_id", "id", "guild_id", "available":
                    break;

                case "volume":
                    eb.addField("Old Volume", "╰┈➤"+oldValue, false);
                    eb.addField("New Volume", "╰┈➤"+newValue, false);
                    break;

                case "emoji_name":
                    eb.addField("Old Related Emoji", "╰┈➤"+oldValue, false);
                    eb.addField("New Related Emoji", "╰┈➤"+newValue, false);
                    break;

                case "emoji_id":
                    eb.addField("Old Related Emoji ID", "╰┈➤"+oldValue, false);
                    eb.addField("New Related Emoji ID", "╰┈➤"+newValue, false);
                    break;

                case "name":
                    eb.addField("Old Sound Item Name", "╰┈➤"+oldValue, false);
                    eb.addField("New Sound Item Name", "╰┈➤"+newValue, false);
                    break;


                default:
                    eb.addField(change, "from "+oldValue+" to "+newValue, false);
            }

        }

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
