package org.papertrail.listeners.audit.helper;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.papertrail.commons.utilities.MemberRoleUpdateParser;

import java.awt.Color;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class MemberRoleUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry  | Member Role Update");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        User target = ale.getJDA().getUserById(ale.getTargetIdLong());

        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
        String mentionableTarget = (target !=null ? target.getAsMention() : ale.getTargetId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following member role was updated");
        eb.setThumbnail(Objects.requireNonNull(event.getGuild().getMemberById(ale.getTargetIdLong())).getEffectiveAvatarUrl());
        eb.setColor(Color.CYAN);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {

                case "$add":
                    eb.setColor(Color.GREEN);
                    eb.addField("Target Member", "╰┈➤"+mentionableTarget, false);
                    Map<String, String> addedRoleNameAndId = MemberRoleUpdateParser.parseRoleUpdate(newValue);
                    eb.addField("Role Added", "✅ "+addedRoleNameAndId.getOrDefault("name", "`ERROR: Name Not Found`"), false);
                    eb.addField("Added Role ID", "╰┈➤"+addedRoleNameAndId.getOrDefault("id", "`ERROR: ID Not Found`"), false);
                    break;

                case "$remove":
                    eb.setColor(Color.RED);
                    eb.addField("Target Member", "╰┈➤"+mentionableTarget, false);
                    Map<String, String> removedRoleNameAndId = MemberRoleUpdateParser.parseRoleUpdate(newValue);
                    eb.addField("Role Removed", "❌ "+removedRoleNameAndId.getOrDefault("name", "`ERROR: Name Not Found`"), false);
                    eb.addField("Removed Role ID", "╰┈➤"+removedRoleNameAndId.getOrDefault("id", "`ERROR: ID Not Found`"), false);
                    break;

                default:
                    eb.addField(change, "from "+oldValue+" to "+newValue, false);
            }
        }

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();

        Objects.requireNonNull(event.getGuild().getTextChannelById(channelIdToSendTo)).sendMessageEmbeds(mb).queue();
    }
}
