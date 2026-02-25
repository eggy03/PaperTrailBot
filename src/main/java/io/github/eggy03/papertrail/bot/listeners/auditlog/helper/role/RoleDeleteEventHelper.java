package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.role;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.role.utils.RoleUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;

@UtilityClass
@Slf4j
public class RoleDeleteEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Role Delete Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Role Deleted By: " + mentionableExecutor + "\nTarget Role ID: " + ale.getTargetId()));
        eb.setColor(Color.RED);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "name" -> eb.addField(MarkdownUtil.underline("Role Name"), "╰┈➤" + oldValue, false);

                case "hoist" ->
                        eb.addField(MarkdownUtil.underline("Display Separately"), "╰┈➤" + BooleanUtils.formatToYesOrNo(oldValue), false);

                case "color" ->
                        eb.addField(MarkdownUtil.underline("Color"), "╰┈➤" + RoleUtils.formatToHex(oldValue), false);

                case "permissions" ->
                        eb.addField(MarkdownUtil.underline("Role Permissions"), RoleUtils.resolveRolePermissions(oldValue, "✅"), false);

                case "mentionable" ->
                        eb.addField(MarkdownUtil.underline("Mentionable"), "╰┈➤" + BooleanUtils.formatToYesOrNo(oldValue), false);

                case "colors" ->
                        eb.addField(MarkdownUtil.underline("Gradient Color System"), RoleUtils.formatGradientToHex(oldValue), false);

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
