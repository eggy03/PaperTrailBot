package io.github.eggy03.papertrail.bot.listeners.audit.helper.channel;

import io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.utils.ChannelUtils;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
public class ChannelOverrideDeleteEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());
        String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Channel Override Delete");

        eb.setDescription("ℹ️ The following channel overrides were deleted by: "+mentionableExecutor);
        eb.setColor(Color.RED);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "type" -> eb.addField("Override Type", "╰┈➤"+ ChannelUtils.resolveChannelOverrideTargetType(oldValue), false);

                case "deny" -> eb.addField("Denied Permissions", ChannelUtils.resolvePermissions(oldValue, "❌"), false);
                case "allow" -> eb.addField("Allowed Permissions", ChannelUtils.resolvePermissions(oldValue, "✅"), false);

                // id exposes the member/role id which for which the channel permissions are overridden
                // only one member/role permission id is fetched per loop
                case "id" -> eb.addField("Target", "╰┈➤"+ChannelUtils.resolveMemberOrRole(oldValue, event), false);

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
}
