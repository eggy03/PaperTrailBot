package org.papertrail.listeners.audit.helper;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;
import java.util.List;
import java.util.Map;

@UtilityClass
public class AutoModerationRuleUpdateEventHelper {

    public static void format (GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | AutoMod Rule Update");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following AutoMod rule was updated");
        eb.setColor(Color.YELLOW);

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
                    eb.addField("✔️ New Exempt Roles: ", "╰┈➤"+ mentionableRoles, false);
                    break;


                case "actions":
                    eb.addField("⚡ New Actions", "╰┈➤"+newValue, false);
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
                    eb.addField("✔️ New Exempt Channels: ", "╰┈➤"+ mentionableChannels, false);
                    break;


                case "trigger_metadata":
                    eb.addField("📊 New Trigger Metadata", "╰┈➤"+newValue, false);
                    break;

                default:
                    eb.addField(change, "from "+oldValue+" to "+newValue, false);
            }
        }

        // add name of the rule which got updated
        AutoModRule rule = ale.getGuild().retrieveAutoModRuleById(ale.getTargetId()).complete();
        eb.addField("🏷️ AutoMod Rule Name ", "╰┈➤"+rule.getName(), false);

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
