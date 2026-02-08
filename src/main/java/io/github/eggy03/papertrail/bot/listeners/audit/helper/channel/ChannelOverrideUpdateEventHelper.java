package io.github.eggy03.papertrail.bot.listeners.audit.helper.channel;

import io.github.eggy03.papertrail.bot.commons.utilities.PermissionResolver;
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

import java.awt.Color;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class ChannelOverrideUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Channel Override Update");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());

        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
        String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following channel overrides were updated");
        eb.setColor(Color.YELLOW);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        // changes do not expose the id and type keys in case of override updates
        String overriddenId = ale.getOptionByName("id");
        String overriddenType = ale.getOptionByName("type");

        String mentionableOverrideTarget = overriddenId;
        if ("0".equals(overriddenType)) {
            // It’s a role
            Role role = event.getGuild().getRoleById(Objects.requireNonNull(overriddenId));
            if (role != null) {
                mentionableOverrideTarget = role.getAsMention();
            }
        } else if ("1".equals(overriddenType)) {
            // It’s a member
            Member member = event.getGuild().getMemberById(Objects.requireNonNull(overriddenId));
            if (member != null) {
                mentionableOverrideTarget = member.getAsMention();
            }
        }

        eb.addField("🎭 Permissions Overridden For", "╰┈➤"+mentionableOverrideTarget, false);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {

                case "deny":
                    String deniedPerms = PermissionResolver.getParsedPermissions(newValue, "❌");
                    // if a channel is synchronized with it's category, the permission list will be blank and the StringBuilder will return a blank string
                    eb.addField("Denied Permissions", (deniedPerms.isBlank() ? "Permissions Synced With Category" : deniedPerms), false);
                    break;

                case "allow":
                    String allowedPerms = PermissionResolver.getParsedPermissions(newValue, "✅");
                    // if a channel is synchronized with it's category, the permission list will be blank and the StringBuilder will return a blank string
                    eb.addField("Allowed Permissions", (allowedPerms.isBlank() ? "Permissions Synced With Category" : allowedPerms), false);
                    break;

                default:
                    eb.addField(change, "from "+oldValue+" to "+newValue, false);
            }
        }

        // add the target channel whose permissions were overridden
        // can be retrieved via ALE's TargetID
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
