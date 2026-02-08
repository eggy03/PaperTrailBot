package io.github.eggy03.papertrail.bot.listeners.audit.helper.invite;

import io.github.eggy03.papertrail.bot.commons.utilities.BooleanFormatter;
import io.github.eggy03.papertrail.bot.commons.utilities.DurationFormatter;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
public class InviteCreateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Invite Create Event");

        eb.setDescription("ℹ️ The following invite was created");
        eb.setColor(Color.CYAN);
        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        ale.getChanges().forEach((changeKey, changeValue)-> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "code" -> eb.addField("Invite Code", "╰┈➤"+newValue, false);

                case "inviter_id" -> {
                    User inviter = ale.getJDA().getUserById(ale.getUserIdLong());
                    String mentionableInviter = (inviter != null ? inviter.getAsMention() : ale.getUserId());
                    eb.addField("Invite Created By", "╰┈➤"+ mentionableInviter, false);
                }

                case "temporary" -> eb.addField("Temporary Invite", "╰┈➤"+ BooleanFormatter.formatToYesOrNo(newValue), false);

                case "max_uses" -> {
                    int maxUses = Integer.parseInt(String.valueOf(newValue));
                    eb.addField("Max Uses", "╰┈➤"+(maxUses == 0 ? "Unlimited" : String.valueOf(maxUses)), false);
                }

                case "uses", "flags" -> {
                    // ignore
                }

                case "max_age" -> eb.addField("Expires After", "╰┈➤"+ DurationFormatter.formatSeconds(newValue), false);

                case "channel_id" -> {
                    GuildChannel channel = ale.getGuild().getGuildChannelById(String.valueOf(newValue));
                    eb.addField("Invite Channel", "╰┈➤"+(channel != null ? channel.getAsMention() : "`"+newValue+"`"), false);
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
