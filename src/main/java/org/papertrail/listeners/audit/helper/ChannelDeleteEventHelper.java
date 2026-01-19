package org.papertrail.listeners.audit.helper;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.papertrail.commons.utilities.TypeResolver;

import java.awt.Color;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class ChannelDeleteEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Channel Delete Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        GuildChannel targetChannel = ale.getJDA().getGuildChannelById(ale.getTargetIdLong());

        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
        String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId()); // this will return only the ID cause the channel with the ID has been deleted

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following channel was deleted");
        eb.setColor(Color.RED);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);


        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {
                case "name":
                    eb.addField("🏷️ Name", "╰┈➤"+oldValue, false);
                    break;

                case "type":
                    eb.addField("🗨️ Type", "╰┈➤"+ TypeResolver.channelTypeResolver(oldValue), false);
                    break;

                case "user_limit", "rate_limit_per_user", "nsfw", "permission_overwrites", "video_quality_mode", "flags", "bitrate", "rtc_region":
                    break;  // they're null during channel delete events

                default:
                    eb.addField(change, "from "+oldValue+" to "+newValue, false);
            }
        }

        eb.addField("🆔 Deleted Channel ID", "╰┈➤"+mentionableTargetChannel, false);
        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
