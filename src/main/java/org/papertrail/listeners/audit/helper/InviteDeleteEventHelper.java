package org.papertrail.listeners.audit.helper;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class InviteDeleteEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Invite Delete Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following invite has been deleted");
        eb.setColor(Color.BLUE);
        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {

                case "code":
                    eb.addField("🔗 Deleted Invite Code", "╰┈➤"+oldValue, false);
                    break;

                case "inviter_id":
                    User inviter = ale.getJDA().getUserById(String.valueOf(oldValue));
                    eb.addField("👤 Invite Originally Created By", "╰┈➤"+(inviter != null ? inviter.getAsMention() : "`Unknown`"), false);
                    break;

                case "temporary":
                    eb.addField("🕒 Temporary Invite", "╰┈➤"+((Boolean.TRUE.equals(oldValue)) ? "✅" : "❌"), false);
                    break;

                case "max_uses", "flags", "max_age":
                    break;
                case "uses":
                    eb.addField("🔢 Number of times the invite was used", "╰┈➤"+oldValue, false);
                    break;
                case "channel_id":
                    Channel channel = ale.getGuild().getGuildChannelById(String.valueOf(oldValue));
                    eb.addField("💬 Invite Channel", "╰┈➤"+(channel != null ? channel.getAsMention() : "`"+oldValue+"`"), false);
                    break;
                default:
                    eb.addField(change, "from "+oldValue+" to "+newValue, false);
            }
        }

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();

        Objects.requireNonNull(event.getGuild().getTextChannelById(channelIdToSendTo)).sendMessageEmbeds(mb).queue();
    }
}
