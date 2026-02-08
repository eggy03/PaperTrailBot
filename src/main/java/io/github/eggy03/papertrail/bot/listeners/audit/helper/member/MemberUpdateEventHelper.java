package io.github.eggy03.papertrail.bot.listeners.audit.helper.member;

import io.github.eggy03.papertrail.bot.commons.utilities.BooleanFormatter;
import io.github.eggy03.papertrail.bot.commons.utilities.DurationFormatter;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class MemberUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Member Update Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        User target = ale.getJDA().getUserById(ale.getTargetIdLong());

        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
        String mentionableTarget = (target !=null ? target.getAsMention() : ale.getTargetId());

        Member targetMember = ale.getGuild().getMemberById(ale.getTargetId());
        String mentionableTargetEffectiveName = targetMember!=null ? targetMember.getEffectiveName() : "Name could not be fetched";

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following member was updated");
        eb.setThumbnail(Objects.requireNonNull(event.getGuild().getMemberById(ale.getTargetId())).getEffectiveAvatarUrl());
        eb.setColor(Color.CYAN);
        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {

                case "communication_disabled_until":
                    if(newValue==null) {
                        eb.setColor(Color.GREEN);
                        eb.addField("🟢 Timeout Lifted", "╰┈➤ Timeout for "+mentionableTarget+ " has been removed", false);
                    } else {
                        eb.setColor(Color.YELLOW);
                        eb.addField("⛔ Timeout Received", "╰┈➤"+mentionableTarget+ " has received a timeout", false);
                        eb.addField("⏱️ Till", "╰┈➤"+ DurationFormatter.isoToLocalTimeCounter(newValue), false);
                        eb.addField("📝 Reason", "╰┈➤"+(ale.getReason()!=null ? ale.getReason() : "No Reason Provided"), false);
                    }

                    break;

                case "nick":
                    if(oldValue!=null && newValue==null) { // resetting to default nickname
                        eb.addField("🏷️ Target", "╰┈➤"+mentionableTarget, false);
                        eb.addField("🏷️ Old Nickname", "╰┈➤"+oldValue, false);
                        eb.addField("🏷️ Reset Name To", "╰┈➤"+mentionableTargetEffectiveName, false);
                    } else if(oldValue != null) { // changing from one nickname to another
                        eb.addField("🏷️ Target", "╰┈➤"+mentionableTarget, false);
                        eb.addField("🏷️ Old Nickname", "╰┈➤"+oldValue, false);
                        eb.addField("🏷️ New Nickname", "╰┈➤"+newValue, false);
                    } else if(newValue != null) { // changing from default nickname to a new nickname
                        eb.addField("🏷️ Target", "╰┈➤"+mentionableTarget, false);
                        eb.addField("🏷️ Nickname Added", "╰┈➤"+ newValue, false);
                    }
                    break;

                case "mute":
                    eb.addField("🎙️ Is Muted", "╰┈➤Set "+mentionableTarget+"'s Mute Status as "+ BooleanFormatter.formatToEmoji(newValue), false);
                    break;

                case "deaf":
                    eb.addField("🔇 Is Deafened", "╰┈➤Set "+mentionableTarget+"'s Deafened Status as "+ BooleanFormatter.formatToEmoji(newValue), false);
                    break;

                case "bypasses_verification":
                    eb.addField("🛡️ Bypass Verification", "╰┈➤Set "+mentionableTarget+"'s verification bypass status as "+ BooleanFormatter.formatToEmoji(newValue), false);
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
