package org.papertrail.listeners.audit.helper;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.papertrail.commons.utilities.BooleanFormatter;

import java.awt.Color;

@UtilityClass
public class InviteDeleteEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Invite Delete Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription("ℹ️ The following invite has been deleted by: "+mentionableExecutor);
        eb.setColor(Color.BLUE);
        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        ale.getChanges().forEach((changeKey, changeValue)-> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "code" -> eb.addField("Deleted Invite Code", "╰┈➤"+oldValue, false);

                case "inviter_id" -> {
                    User inviter = ale.getJDA().getUserById(String.valueOf(oldValue));
                    eb.addField("Invite Originally Created By", "╰┈➤"+(inviter != null ? inviter.getAsMention() : "`Unknown`"), false);
                }

                case "temporary" -> eb.addField("Temporary Invite", "╰┈➤"+ BooleanFormatter.formatToYesOrNo(oldValue), false);

                case "max_uses", "flags", "max_age" -> {
                    // ignore
                }

                case "uses" -> eb.addField("Number of times the invite was used", "╰┈➤"+oldValue, false);

                case "channel_id" -> {
                    Channel channel = ale.getGuild().getGuildChannelById(String.valueOf(oldValue));
                    eb.addField("Invite Channel", "╰┈➤"+(channel != null ? channel.getAsMention() : "`"+oldValue+"`"), false);
                }

                default -> {
                    eb.addField(changeKey, "OLD_VALUE: "+oldValue, false);
                    eb.addField(changeKey, "NEW_VALUE: "+newValue, false);
                }

            }
        });

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
