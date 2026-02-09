package io.github.eggy03.papertrail.bot.listeners.audit.helper.role;

import io.github.eggy03.papertrail.bot.commons.utilities.BooleanFormatter;
import io.github.eggy03.papertrail.bot.commons.utilities.ColorFormatter;
import io.github.eggy03.papertrail.bot.commons.utilities.PermissionResolver;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;
import java.util.Map;

@UtilityClass
@Slf4j
public class RoleUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Role Update Event");

        User executor = ale.getJDA().getUserById(ale.getUserId());
        Role targetRole = ale.getJDA().getRoleById(ale.getTargetId());

        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
        String mentionableTargetRole = (targetRole !=null ? targetRole.getAsMention() : ale.getTargetId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following member role was updated");
        eb.setColor(Color.YELLOW);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        eb.addField("Target Role", mentionableTargetRole, false);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {

                case "name":
                    eb.addField("🏷️ Old Role Name", "╰┈➤"+oldValue, true);
                    eb.addField("🏷️ New Role Name", "╰┈➤"+newValue, true);
                    eb.addBlankField(true);
                    break;

                case "hoist":
                    eb.addField("📂 Old Display Seperately", "╰┈➤"+ BooleanFormatter.formatToEmoji(oldValue), true);
                    eb.addField("📂 New Display Seperately", "╰┈➤"+BooleanFormatter.formatToEmoji(newValue), true);
                    eb.addBlankField(true);
                    break;

                case "color":
                    eb.addField("🎨 Old Color", "╰┈➤"+ ColorFormatter.formatToHex(oldValue), true);
                    eb.addField("🎨 New Color", "╰┈➤"+ColorFormatter.formatToHex(newValue), true);
                    eb.addBlankField(true);
                    break;

                case "permissions":
                    eb.addField("Old Role Permissions", PermissionResolver.getParsedPermissions(oldValue, "✅"), true);
                    eb.addField("New Role Permissions", PermissionResolver.getParsedPermissions(newValue, "✅"), true);
                    eb.addBlankField(true);
                    break;

                case "mentionable":
                    eb.addField("🔗 Old Mentionable", "╰┈➤"+BooleanFormatter.formatToEmoji(oldValue), true);
                    eb.addField("🔗 New Mentionable", "╰┈➤"+BooleanFormatter.formatToEmoji(newValue), true);
                    eb.addBlankField(true);
                    break;

                case "colors":
                    eb.addField("🌈 Old Gradient Color System", "╰┈➤"+ColorFormatter.formatGradientColorSystemToHex(oldValue), true);
                    eb.addField("🌈 New Gradient Color System", "╰┈➤"+ColorFormatter.formatGradientColorSystemToHex(newValue), true);
                    eb.addBlankField(true);
                    break;
                default:
                    eb.addField(change, "from "+oldValue+" to "+newValue, false);
            }
        }

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();
        if(!mb.isSendable()){
            log.warn("An embed is either empty or has exceed the max length for characters, with current length: {}", eb.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
