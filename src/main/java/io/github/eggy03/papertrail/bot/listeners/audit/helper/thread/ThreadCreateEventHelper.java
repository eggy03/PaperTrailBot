package io.github.eggy03.papertrail.bot.listeners.audit.helper.thread;

import io.github.eggy03.papertrail.bot.commons.utilities.BooleanFormatter;
import io.github.eggy03.papertrail.bot.commons.utilities.DurationFormatter;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.utils.ChannelUtils;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;
import java.util.Map;

@UtilityClass
public class ThreadCreateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Thread Create Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        ThreadChannel targetThread = event.getGuild().getThreadChannelById(ale.getTargetId());
        String mentionableTargetThread = (targetThread !=null ? targetThread.getAsMention() : ale.getTargetId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ A thread has been created");
        eb.setColor(Color.GREEN);
        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {
                case "locked":
                    eb.addField("🔒 Locked", "╰┈➤"+ BooleanFormatter.formatToEmoji(newValue), false);
                    break;

                case "auto_archive_duration":
                    eb.addField("🕒 Auto Archive Duration", "╰┈➤"+ DurationFormatter.formatMinutes(newValue), false);
                    break;

                case "rate_limit_per_user":
                    eb.addField("🐌 Slowmode Limit", "╰┈➤"+DurationFormatter.formatSeconds(newValue), false);
                    break;

                    // TODO make threadutils for resolving thread types
                case "type":
                    eb.addField("📁 Thread Type", "╰┈➤"+ ChannelUtils.resolveChannelType(newValue), false);
                    break;

                case "archived":
                    eb.addField("🗄️ Archived", "╰┈➤"+BooleanFormatter.formatToEmoji(newValue), false);
                    break;

                case "flags":
                    eb.addField("🚩 Flags", "╰┈➤"+newValue, false);
                    break;

                case "name":
                    eb.addField("🏷️ Thread Name", "╰┈➤"+newValue, false);
                    break;

                default:
                    eb.addField(change, "from "+oldValue+" to "+newValue, false);
            }

        }
        eb.addField("🧵 Target Thread", "╰┈➤"+mentionableTargetThread, false);
        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
