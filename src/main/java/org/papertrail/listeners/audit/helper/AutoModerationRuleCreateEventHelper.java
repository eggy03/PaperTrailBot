package org.papertrail.listeners.audit.helper;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.papertrail.commons.utilities.BooleanFormatter;
import org.papertrail.commons.utilities.TypeResolver;

import java.awt.Color;
import java.util.List;
import java.util.Map;

@UtilityClass
public class AutoModerationRuleCreateEventHelper {

    public static void format (GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | AutoMod Rule Create");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following AutoMod rule was created");
        eb.setColor(Color.GREEN);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {

                case "exempt_roles":
                    StringBuilder mentionableRoles = new StringBuilder();
                    if(newValue instanceof List<?> exemptRoleList) {
                        exemptRoleList.forEach(roleId -> {
                            Role r = ale.getGuild().getRoleById((String) roleId);
                            mentionableRoles.append(r!=null ? r.getAsMention() : roleId).append(", ");
                        });
                        mentionableRoles.delete(mentionableRoles.length()-2, mentionableRoles.length());
                    }
                    eb.addField("✔️ Exempt Roles: ", "╰┈➤"+ mentionableRoles, false);
                    break;

                case "enabled":
                    eb.addField("❔ Enabled", "╰┈➤"+ BooleanFormatter.formatToEmoji(newValue), false);
                    break;

                case "trigger_type":
                    eb.addField("⚡ Trigger Type", "╰┈➤"+ TypeResolver.automodTriggerTypeResolver(newValue), false);
                    break;

                case "actions":
                    eb.addField("⚡ Actions", "╰┈➤"+newValue, false);
                    break;

                case "exempt_channels":
                    StringBuilder mentionableChannels = new StringBuilder();
                    if(newValue instanceof List<?> exemptChannelList) {
                        exemptChannelList.forEach(channelId -> {
                            GuildChannel r = ale.getGuild().getGuildChannelById((String) channelId);
                            mentionableChannels.append(r!=null ? r.getAsMention() : channelId).append(", ");
                        });
                        mentionableChannels.delete(mentionableChannels.length()-2, mentionableChannels.length());
                    }
                    eb.addField("✔️ Exempt Channels: ", "╰┈➤"+ mentionableChannels, false);
                    break;

                case "event_type":
                    eb.addField("🧭 Event Type", "╰┈➤"+TypeResolver.automodEventTypeResolver(newValue), false);
                    break;

                case "trigger_metadata":
                    eb.addField("📊 Trigger Metadata", "╰┈➤"+newValue, false);
                    break;

                case "name":
                    eb.addField("🏷️ AutoMod Rule Name ", "╰┈➤"+newValue, false);
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
