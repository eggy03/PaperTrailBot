package io.github.eggy03.papertrail.bot.listeners.audit.helper.thread;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.commons.utils.DurationUtils;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.utils.ChannelUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ThreadUpdateEventHelper {

   public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

       AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Thread Update Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        ThreadChannel targetThread = event.getGuild().getThreadChannelById(ale.getTargetId());
        String mentionableTargetThread = (targetThread !=null ? targetThread.getAsMention() : ale.getTargetId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ A thread has been updated");
        eb.setColor(Color.YELLOW);
        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);
        eb.addBlankField(true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {
                case "locked":
                    eb.addField("🔒 Old Lock Status", "╰┈➤"+ BooleanUtils.formatToEmoji(oldValue), true);
                    eb.addField("🔒 New Lock Status", "╰┈➤"+ BooleanUtils.formatToEmoji(newValue), true);
                    eb.addBlankField(true);
                    break;

                case "auto_archive_duration":
                    eb.addField("🕒 Old Auto Archive Duration", "╰┈➤"+ DurationUtils.formatMinutes(oldValue), true);
                    eb.addField("🕒 New Auto Archive Duration", "╰┈➤"+ DurationUtils.formatMinutes(newValue), true);
                    eb.addBlankField(true);
                    break;

                case "rate_limit_per_user":
                    eb.addField("🐌 Old Slowmode Limit", "╰┈➤"+ DurationUtils.formatSeconds(oldValue), true);
                    eb.addField("🐌 New Slowmode Limit", "╰┈➤"+ DurationUtils.formatSeconds(newValue), true);
                    eb.addBlankField(true);
                    break;

                    // TODO make threadutils
                case "type":
                    eb.addField("📁 Old Thread Type", "╰┈➤"+ChannelUtils.resolveChannelType(oldValue), true);
                    eb.addField("📁 New Thread Type", "╰┈➤"+ChannelUtils.resolveChannelType(newValue), true);
                    eb.addBlankField(true);
                    break;

                case "archived":
                    eb.addField("🗄️ Old Archive Status", "╰┈➤"+ BooleanUtils.formatToEmoji(oldValue), true);
                    eb.addField("🗄️ New Archive Status", "╰┈➤"+ BooleanUtils.formatToEmoji(newValue), true);
                    eb.addBlankField(true);
                    break;

                case "flags":
                    eb.addField("🚩 Old Flag Value", "╰┈➤"+oldValue, true);
                    eb.addField("🚩 New Flag Value", "╰┈➤"+newValue, true);
                    eb.addBlankField(true);
                    break;

                case "name":
                    eb.addField("🏷️ Old Thread Name", "╰┈➤"+oldValue, true);
                    eb.addField("🏷️ New Thread Name", "╰┈➤"+newValue, true);
                    eb.addBlankField(true);
                    break;

                default:
                    eb.addField(change, "from "+oldValue+" to "+newValue, false);
            }

        }
        eb.addField("🧵 Target Thread", "╰┈➤"+mentionableTargetThread, false);
        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

       MessageEmbed mb = eb.build();
       if(!mb.isSendable()){
           log.warn("Embed is empty or too long (current length: {}).", eb.length());
           return;
       }

       TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
       if(sendingChannel!=null && sendingChannel.canTalk()) {
           sendingChannel.sendMessageEmbeds(mb).queue();
       }
    }
}
