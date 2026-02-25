package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.role;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.role.utils.RoleUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

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
        eb.setDescription(MarkdownUtil.quoteBlock("Role Updated By: " + mentionableExecutor + "\nUpdated Role: " + mentionableTargetRole));
        eb.setColor(Color.YELLOW);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "name" -> {
                    eb.addField(MarkdownUtil.underline("Old Role Name"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Role Name"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }

                case "hoist" -> {
                    eb.addField(MarkdownUtil.underline("Old Display Separately"), "╰┈➤" + BooleanUtils.formatToYesOrNo(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Display Separately"), "╰┈➤" + BooleanUtils.formatToYesOrNo(newValue), true);
                    eb.addBlankField(true);
                }

                case "color" -> {
                    eb.addField(MarkdownUtil.underline("Old Color"), "╰┈➤" + RoleUtils.formatToHex(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Color"), "╰┈➤" + RoleUtils.formatToHex(newValue), true);
                    eb.addBlankField(true);
                }

                case "permissions" -> {
                    eb.addField(MarkdownUtil.underline("Old Role Permissions"), RoleUtils.resolveRolePermissions(oldValue, "✅"), true);
                    eb.addField(MarkdownUtil.underline("New Role Permissions"), RoleUtils.resolveRolePermissions(newValue, "✅"), true);
                    eb.addBlankField(true);
                }

                case "mentionable" -> {
                    eb.addField(MarkdownUtil.underline("Old Mentionable"), "╰┈➤" + BooleanUtils.formatToYesOrNo(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Mentionable"), "╰┈➤" + BooleanUtils.formatToYesOrNo(newValue), true);
                    eb.addBlankField(true);
                }

                case "colors" -> {
                    eb.addField(MarkdownUtil.underline("Old Gradient Color System"), RoleUtils.formatGradientToHex(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Gradient Color System"), RoleUtils.formatGradientToHex(newValue), true);
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

        if (!eb.isValidLength() || eb.isEmpty()) {
            log.warn("Embed is empty or too long (current length: {}).", eb.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }
}
