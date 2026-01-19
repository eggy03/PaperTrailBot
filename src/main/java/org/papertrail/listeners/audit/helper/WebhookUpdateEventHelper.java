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

import java.awt.Color;
import java.util.Map;

@UtilityClass
public class WebhookUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Webhook Update Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ A webhook has been updated");
        eb.setColor(Color.YELLOW);
        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {
                case "type":
                    eb.addField("📡 Webhook Type", "╰┈➤ " + newValue, false);
                    eb.addField("Webhook Type Legend", "-# 0 for PING; 1 for Event", false);
                    break;

                case "avatar_hash":
                    eb.addField("🖼️ Avatar Hash", "╰┈➤"+"from `"+oldValue+"` to `"+newValue+"`", false);
                    break;

                case "channel_id":
                    GuildChannel targetChannel = event.getGuild().getGuildChannelById(String.valueOf(newValue));
                    String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : String.valueOf(newValue));
                    eb.addField("💬 New Channel", "╰┈➤"+mentionableTargetChannel, false);
                    break;

                case "name":
                    eb.addField("🏷️ Webhook Name", "╰┈➤"+"from "+oldValue+" to "+newValue, false);
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
