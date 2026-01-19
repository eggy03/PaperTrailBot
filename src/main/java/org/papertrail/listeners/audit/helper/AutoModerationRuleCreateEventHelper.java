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
import org.apache.commons.lang3.StringUtils;
import org.papertrail.commons.utilities.TypeResolver;

import java.awt.Color;
import java.util.Map;

@UtilityClass
public class AutoModerationRuleCreateEventHelper {

    public static void format (GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

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
                    String roleIds = String.valueOf(newValue);
                    String cleanedRoleIds = StringUtils.strip(roleIds, "[]");
                    String[] roleIdList = StringUtils.split(cleanedRoleIds, ",");
                    StringBuilder mentionableRoles = new StringBuilder();
                    for(String roleId : roleIdList) {
                        Role r = ale.getGuild().getRoleById(roleId.strip());
                        mentionableRoles.append(r!=null ? r.getAsMention() : roleId.strip()).append(", ");
                    }
                    eb.addField("✔️ Exempt Roles: ", "╰┈➤"+ mentionableRoles, false);
                    break;

                case "enabled":
                    eb.addField("❔ Enabled", "╰┈➤"+((Boolean.TRUE.equals(newValue)) ? "✅" : "❌"), false);
                    break;

                case "trigger_type":
                    eb.addField("⚡ Trigger Type", "╰┈➤"+ TypeResolver.automodTriggerTypeResolver(newValue), false);
                    break;

                case "actions":
                    eb.addField("⚡ Actions", "╰┈➤"+newValue, false);
                    break;

                case "exempt_channels":
                    String channelIds = String.valueOf(newValue);
                    String cleanedChannelIds = StringUtils.strip(channelIds, "[]");
                    String[] channelIdList = StringUtils.split(cleanedChannelIds, ",");
                    StringBuilder mentionableChannels = new StringBuilder();
                    for(String channelId : channelIdList) {
                        GuildChannel r = ale.getGuild().getGuildChannelById(channelId.strip());
                        mentionableChannels.append(r!=null ? r.getAsMention() : channelId.strip()).append(", ");
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
