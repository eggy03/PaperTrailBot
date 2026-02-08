package io.github.eggy03.papertrail.bot.listeners.audit.helper.webhook;

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
public class WebhookRemoveEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Webhook Remove Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ A webhook has been removed");
        eb.setColor(Color.RED);
        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {
            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {
                case "type":
                    eb.addField("📡 Webhook Type", "╰┈➤"+oldValue, false);
                    break;

                case "avatar_hash":
                    eb.addField("🖼️ Avatar Hash", "╰┈➤"+oldValue, false);
                    break;

                case "channel_id":
                    GuildChannel targetChannel = event.getGuild().getGuildChannelById(String.valueOf(oldValue));
                    String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : String.valueOf(oldValue));
                    eb.addField("💬 Channel", "╰┈➤"+mentionableTargetChannel, false);
                    break;

                case "name":
                    eb.addField("🏷️ Webhook Name", "╰┈➤"+oldValue, false);
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
