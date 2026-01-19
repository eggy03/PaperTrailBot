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

@UtilityClass
public class ChannelUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Channel Update Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());

        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
        String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following channel was updated");
        eb.setColor(Color.YELLOW);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);
        eb.addBlankField(true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {
                case "user_limit":
                    eb.addField("👥 Old User Limit", "╰┈➤"+ TypeResolver.formatNumberOrUnlimited(oldValue), true);
                    eb.addField("👥 New User Limit", "╰┈➤"+TypeResolver.formatNumberOrUnlimited(newValue), true);
                    eb.addBlankField(true);
                    break;

                case "rate_limit_per_user":
                    eb.addField("🕓 Old Slowmode Value", "╰┈➤"+ DurationFormatter.formatSeconds(oldValue), true);
                    eb.addField("🕓 New Slowmode Value", "╰┈➤"+DurationFormatter.formatSeconds(newValue), true);
                    eb.addBlankField(true);
                    break;

                case "nsfw":
                    eb.addField("🔞 Old NSFW Settings", "╰┈➤"+((Boolean.TRUE.equals(oldValue)) ? "✅" : "❌"), true);
                    eb.addField("🔞 New NSFW Settings", "╰┈➤"+((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), true);
                    eb.addBlankField(true);
                    break;

                case "video_quality_mode":
                    eb.addField("🎥 Old Video Quality Mode", "╰┈➤"+TypeResolver.videoQualityModeResolver(oldValue), true);
                    eb.addField("🎥 New Video Quality Mode", "╰┈➤"+TypeResolver.videoQualityModeResolver(newValue), true);
                    eb.addBlankField(true);
                    break;

                case "name":
                    eb.addField("🏷️ Old Channel Name", "╰┈➤"+oldValue, true);
                    eb.addField("🏷️ New Channel Name", "╰┈➤"+newValue, true);
                    eb.addBlankField(true);
                    break;

                case "bitrate":
                    eb.addField("🎚️ Old Voice Channel Bitrate", "╰┈➤"+TypeResolver.voiceChannelBitrateResolver(oldValue), true);
                    eb.addField("🎚️ New Voice Channel Bitrate", "╰┈➤"+TypeResolver.voiceChannelBitrateResolver(newValue), true);
                    eb.addBlankField(true);
                    break;

                case "rtc_region":
                    eb.addField("🌐 Old Region", "╰┈➤"+oldValue, true);
                    eb.addField("🌐 New Region", "╰┈➤"+newValue, true);
                    eb.addBlankField(true);
                    break;

                case "topic":
                    eb.addField("🗒️ Old Topic", "╰┈➤"+oldValue, true);
                    eb.addField("🗒️ New topic", "╰┈➤"+newValue, true);
                    eb.addBlankField(true);
                    break;

                case "default_auto_archive_duration":
                    eb.addField("🕒 Old Hide After Inactivity Timer", "╰┈➤"+DurationFormatter.formatMinutes(oldValue), true);
                    eb.addField("🕒 New Hide After Inactivity Timer", "╰┈➤"+DurationFormatter.formatMinutes(newValue), true);
                    eb.addBlankField(true);
                    break;

                default:
                    eb.addField(change, "from "+oldValue+" to "+newValue, false);
            }
        }
        // mention the channel that got updated, id can be exposed via ALE's TargetID
        eb.addField("💬 Target Channel", "╰┈➤"+mentionableTargetChannel, false);

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
