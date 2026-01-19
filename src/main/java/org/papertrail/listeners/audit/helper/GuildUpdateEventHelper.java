package org.papertrail.listeners.audit.helper;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.papertrail.commons.utilities.GuildSystemChannelFlagResolver;
import org.papertrail.commons.utilities.TypeResolver;

import java.awt.Color;
import java.util.Map;

@UtilityClass
public class GuildUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Guild Update Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following guild updates were recorded");
        eb.setColor(Color.MAGENTA);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {

                case "description":
                    eb.addField("Description Change", "from `"+oldValue+"` to `"+newValue+"`", false);
                    break;

                case "icon_hash":
                    eb.addField("Icon Hash Change", "from `"+oldValue+"` to `"+newValue+"`", false);
                    break;

                case "name":
                    eb.addField("Guild Name Change", "from `"+oldValue+"` to `"+newValue+"`", false);
                    break;

                case "afk_channel_id":
                    eb.addField("AFK Channel Changed To", "`"+newValue+"`", false);
                    break;

                case "default_message_notifications":
                    eb.addField("Default Message Notifications Update", "`"+newValue+"`", false);
                    break;

                case "afk_timeout":
                    eb.addField("AFK Channel Timeout Change", "`"+newValue+"s`", false);
                    break;

                case "system_channel_id":
                    eb.addField("Community Updates Channel Changed To", "`"+newValue+"`", false);
                    break;

                case "widget_enabled":
                    eb.addField("Widget Enabled", "`"+newValue+"`", false);
                    break;

                case "widget_channel_id":
                    eb.addField("Widget Channel Changed To", "`"+newValue+"`", false);
                    break;

                case "premium_progress_bar_enabled":
                    eb.addField("Server Boost Progress Bar Enabled", "`"+newValue+"`", false);
                    break;

                case "mfa_level":
                    eb.addField("MFA Requirement", "`"+newValue+"`", false);
                    break;

                case "verification_level":
                    eb.addField("Verification Level", "`"+ TypeResolver.guildVerificationLevelResolver(newValue)+"`", false);
                    break;

                case "owner_id":
                    User oldOwner = ale.getJDA().getUserById(String.valueOf(oldValue));
                    User newOwner = ale.getJDA().getUserById(String.valueOf(newValue));
                    String mentionableOldOwner = (oldOwner!=null ? oldOwner.getAsMention() : String.valueOf(oldValue));
                    String mentionableNewOwner = (newOwner!=null ? newOwner.getAsMention() : String.valueOf(newValue));
                    eb.addField("Ownership Change", "from "+mentionableOldOwner+" to "+mentionableNewOwner, false);
                    break;

                case "public_updates_channel_id":
                    eb.addField("Announcements Channel Changed To", "`"+newValue+"`", false);
                    break;

                case "rules_channel_id":
                    eb.addField("Rules Channel Changed To", "`"+newValue+"`", false);
                    break;


                case "system_channel_flags":
                    eb.addField("System Channel Flags", GuildSystemChannelFlagResolver.getParsedFlags(newValue), false);
                    break;

                case "explicit_content_filter":
                    eb.addField("Explicit Content Filter", TypeResolver.explicitFilterTypeResolver(newValue), false);
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
