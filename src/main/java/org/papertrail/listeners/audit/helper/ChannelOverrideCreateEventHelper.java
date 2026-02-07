package org.papertrail.listeners.audit.helper;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.papertrail.commons.utilities.PermissionResolver;
import org.papertrail.commons.utilities.TypeResolver;

import java.awt.Color;
import java.util.Map;

@UtilityClass
public class ChannelOverrideCreateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Channel Override Create");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());

        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
        String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following channel overrides were created");
        eb.setColor(Color.GREEN);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);


        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {

                case "type":
                    eb.addField("🧩 Override Type", "╰┈➤"+ TypeResolver.channelOverrideTypeResolver(newValue), false);
                    break;

                case "deny":
                    // the oldValue will return null if a new channel is over-riden for the first time but we're not concerned with oldValue
                    // the new value contains the list of denied permissions the moderator sets when creating overrides for the first time
                    eb.addField("Denied Permissions", PermissionResolver.getParsedPermissions(newValue, "❌"), false);
                    break;

                case "allow":
                    // the oldValue will return null if a new channel is over-riden for the first time but we're not concerned with oldValue
                    // the new value contains the list of allowed permissions the moderator sets when creating overrides for the first time
                    eb.addField("Allowed Permissions", PermissionResolver.getParsedPermissions(newValue, "✅"), false);
                    break;

                case "id":
                    // id exposes the member/role id which for which the channel permissions are over-riden
                    // only one member/role permissions can be over-riden at a time
                    Member mb = event.getGuild().getMemberById(String.valueOf(newValue));
                    Role r = event.getGuild().getRoleById(String.valueOf(newValue));

                    String mentionableRoleOrMember = "";
                    if(mb!=null) {
                        mentionableRoleOrMember = mb.getAsMention();
                    } else if (r!=null) {
                        mentionableRoleOrMember = r.getAsMention();
                    }
                    eb.addField("🎭 Target", "╰┈➤"+mentionableRoleOrMember, false);
                    break;

                default:
                    eb.addField(change, "from "+oldValue+" to "+newValue, false);
            }
        }
        // add the target channel whose permissions were overridden
        // exposed via ALE's TargetID
        eb.addField("🗨️ Target Channel", "╰┈➤"+mentionableTargetChannel, false);

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
