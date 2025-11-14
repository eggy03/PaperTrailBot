package org.papertrail.listeners.audit.helper;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.papertrail.commons.utilities.ColorFormatter;
import org.papertrail.commons.utilities.PermissionResolver;

import java.awt.Color;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class RoleDeleteEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Role Delete Event");

        User executor = ale.getJDA().getUserById(ale.getUserId());
        Role targetRole = ale.getJDA().getRoleById(ale.getTargetId());

        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
        String mentionableTargetRole = (targetRole !=null ? targetRole.getAsMention() : ale.getTargetId()); // this will always return the ID only

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following role was deleted");
        eb.setColor(Color.RED);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {

                case "name":
                    eb.addField("🏷️ Role Name", "╰┈➤"+oldValue, false);
                    break;

                case "hoist":
                    eb.addField("📂 Display Seperately", "╰┈➤"+((Boolean.TRUE.equals(oldValue)) ? "✅" : "❌"), false);
                    break;

                case "color":
                    eb.addField("🎨 Color", "╰┈➤"+ ColorFormatter.formatToHex(oldValue), false);
                    break;

                case "permissions":
                    eb.addField("Role Permissions", PermissionResolver.getParsedPermissions(oldValue, "✅"), false);
                    break;

                case "mentionable":
                    eb.addField("🔗 Mentionable", "╰┈➤"+((Boolean.TRUE.equals(oldValue)) ? "✅" : "❌"), false);
                    break;

                case "colors":
                    eb.addField("🌈 Gradient Color System", "╰┈➤"+ColorFormatter.formatGradientColorSystemToHex(oldValue), false);
                    break;
                default:
                    eb.addField(change, "from "+oldValue+" to "+newValue, false);
            }
        }

        eb.addField("🆔 Deleted Role ID", "╰┈➤"+mentionableTargetRole, false);
        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();

        Objects.requireNonNull(event.getGuild().getTextChannelById(channelIdToSendTo)).sendMessageEmbeds(mb).queue();
    }
}
