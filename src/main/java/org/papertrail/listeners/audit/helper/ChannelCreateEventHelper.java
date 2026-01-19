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
import org.papertrail.commons.utilities.DurationFormatter;
import org.papertrail.commons.utilities.TypeResolver;

import java.awt.Color;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class ChannelCreateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Channel Create Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        GuildChannel targetChannel = ale.getGuild().getGuildChannelById(ale.getTargetId());

        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
        String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId());


        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following channel was created");
        eb.setColor(Color.GREEN);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {
                case "user_limit":
                    eb.addField("👥 User Limit", "╰┈➤"+ TypeResolver.formatNumberOrUnlimited(newValue), false);
                    break;

                case "rate_limit_per_user":
                    eb.addField("🕓 Slowmode", "╰┈➤"+ DurationFormatter.formatSeconds(newValue), false);
                    break;

                case "type":
                    eb.addField("🗨️ Channel Type", "╰┈➤"+TypeResolver.channelTypeResolver(newValue), false);
                    break;

                case "nsfw":
                    eb.addField("🔞 NSFW", "╰┈➤"+((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), false);
                    break;

                case "permission_overwrites", "flags":
                    break;

                case "name":
                    eb.addField("🏷️ Channel Name", "╰┈➤"+newValue, false);
                    // provide a channel link next to its name. This mentionable channel can be obtained via the target ID of ALE
                    eb.addField("🔗 Channel Link", "╰┈➤"+mentionableTargetChannel, true);
                    break;

                case "bitrate":
                    eb.addField("🎚️ Voice Channel Bitrate", "╰┈➤"+TypeResolver.voiceChannelBitrateResolver(newValue), false);
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
