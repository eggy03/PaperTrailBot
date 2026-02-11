package io.github.eggy03.papertrail.bot.listeners.audit.helper.role;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.commons.utils.ColorUtils;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.role.utils.RoleUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
@Slf4j
public class RoleUpdateEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        Role targetRole = ale.getJDA().getRoleById(ale.getTargetId());
        String mentionableTargetRole = (targetRole != null ? targetRole.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Role Update Event");
        eb.setDescription("ℹ️ The following role was updated by: " + mentionableExecutor);
        eb.setColor(Color.YELLOW);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        eb.addField("Target Role", "╰┈➤" + mentionableTargetRole, false);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "name" -> {
                    eb.addField("Old Role Name", "╰┈➤" + oldValue, true);
                    eb.addField("New Role Name", "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }

                case "hoist" -> {
                    eb.addField("Old Display Separately", "╰┈➤" + BooleanUtils.formatToYesOrNo(oldValue), true);
                    eb.addField("New Display Separately", "╰┈➤" + BooleanUtils.formatToYesOrNo(newValue), true);
                    eb.addBlankField(true);
                }

                case "color" -> {
                    eb.addField("Old Color", "╰┈➤" + ColorUtils.formatToHex(oldValue), true);
                    eb.addField("New Color", "╰┈➤" + ColorUtils.formatToHex(newValue), true);
                    eb.addBlankField(true);
                }

                case "permissions" -> {
                    eb.addField("Old Role Permissions", RoleUtils.resolveRolePermissions(oldValue, "✅"), true);
                    eb.addField("New Role Permissions", RoleUtils.resolveRolePermissions(newValue, "✅"), true);
                    eb.addBlankField(true);
                }

                case "mentionable" -> {
                    eb.addField("Old Mentionable", "╰┈➤" + BooleanUtils.formatToYesOrNo(oldValue), true);
                    eb.addField("New Mentionable", "╰┈➤" + BooleanUtils.formatToYesOrNo(newValue), true);
                    eb.addBlankField(true);
                }

                case "colors" -> {
                    eb.addField("Old Gradient Color System", ColorUtils.formatGradientColorSystemToHex(oldValue), true);
                    eb.addField("New Gradient Color System", ColorUtils.formatGradientColorSystemToHex(newValue), true);
                    eb.addBlankField(true);
                }

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }

            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();
        if (!mb.isSendable()) {
            log.warn("Embed is empty or too long (current length: {}).", eb.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
