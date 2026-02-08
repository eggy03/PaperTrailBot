package io.github.eggy03.papertrail.bot.listeners.audit.helper.channel;

import io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.utils.ChannelUtils;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
public class ChannelOverrideUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());
        String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Channel Override Update");
        eb.setDescription("ℹ️ The following channel overrides were updated by: "+mentionableExecutor);
        eb.setColor(Color.YELLOW);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        eb.addField("Override Type", "╰┈➤"+ ChannelUtils.resolveChannelOverrideTargetType(ale.getOptionByName("type")), false);
        eb.addField("Permissions Overridden For", "╰┈➤"+getTargetRoleOrMember(ale, event), false);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "deny" -> eb.addField("Denied Permissions", ChannelUtils.resolvePermissions(newValue, "❌"), false);
                case "allow" -> eb.addField("Allowed Permissions", ChannelUtils.resolvePermissions(newValue, "✅"), false);

                default -> {
                    eb.addField(changeKey, "OLD_VALUE: "+oldValue, false);
                    eb.addField(changeKey, "NEW_VALUE: "+newValue, false);
                }

            }
        });

        // add the target channel whose permissions were overridden
        // can be retrieved via ALE's TargetID
        eb.addField("Target Channel", "╰┈➤"+mentionableTargetChannel, false);

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }

    // ALE changes do not expose the id and type keys in case of override updates
    // therefore, this hacky method is required
    // IDK why channel override updates don't show this info
    private static String getTargetRoleOrMember(AuditLogEntry ale, GuildAuditLogEntryCreateEvent event) {

        String overriddenId = ale.getOptionByName("id"); // id of the member or role
        String overriddenType = ale.getOptionByName("type"); // type that determines if the ID is that of a role or a member

        if(overriddenId==null)
            return "Member or Role ID is null";

        return switch (overriddenType) {

            case "0" -> {
                // It’s a role
                Role role = event.getGuild().getRoleById(overriddenId);
                yield role!=null ? role.getAsMention() : overriddenId;
            }

            case "1" -> {
                // It's a member
                Member member = event.getGuild().getMemberById(overriddenId);
                yield member!=null ? member.getAsMention() : overriddenId;
            }

            case null -> "Override Type is null";
            default -> "Unimplemented Override Type: "+overriddenType;

        };
    }
}
