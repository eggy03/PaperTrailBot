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
import org.papertrail.commons.utilities.BooleanFormatter;
import org.papertrail.commons.utilities.DurationFormatter;

import java.awt.Color;
import java.util.Map;

@UtilityClass
public class InviteCreateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Invite Create Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following invite was created");
        eb.setColor(Color.CYAN);
        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {

                case "code":
                    eb.addField("🔗 Invite Code", "╰┈➤"+newValue, false);
                    break;

                case "inviter_id":
                    User inviter = ale.getJDA().getUserById(String.valueOf(newValue));
                    eb.addField("👤 Invite Created By", "╰┈➤"+(inviter != null ? inviter.getAsMention() : ale.getUserId()), false);
                    break;

                case "temporary":
                    eb.addField("🕒 Temporary Invite", "╰┈➤"+ BooleanFormatter.formatToEmoji(newValue), false);
                    break;

                case "max_uses":
                    int maxUses = Integer.parseInt(String.valueOf(newValue));
                    eb.addField("🔢 Max Uses", "╰┈➤"+(maxUses == 0 ? "Unlimited" : String.valueOf(maxUses)), false);
                    break;

                case "uses", "flags":
                    break;

                case "max_age":
                    eb.addField("⏳ Expires After", "╰┈➤"+ DurationFormatter.formatSeconds(newValue), false);
                    break;

                case "channel_id":
                    GuildChannel channel = ale.getGuild().getGuildChannelById(String.valueOf(newValue));
                    eb.addField("💬 Invite Channel", "╰┈➤"+(channel != null ? channel.getAsMention() : "`"+newValue+"`"), false);
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
