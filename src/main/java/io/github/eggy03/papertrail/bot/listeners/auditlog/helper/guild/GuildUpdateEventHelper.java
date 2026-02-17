package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.guild;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.commons.utils.DurationUtils;
import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.guild.utils.GuildUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
@Slf4j
public class GuildUpdateEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Guild Update Event");
        eb.setDescription("ℹ️ The following guild updates were made by: " + mentionableExecutor);
        eb.setColor(Color.MAGENTA);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "description" -> {
                    eb.addField("Old Description", "╰┈➤" + oldValue, false);
                    eb.addField("New Description", "╰┈➤" + newValue, false);
                }

                case "icon_hash" -> eb.addField("Icon Hash Change", "Guild Icon has been updated", false);

                case "name" -> {
                    eb.addField("Old Guild Name", "╰┈➤" + oldValue, false);
                    eb.addField("New Guild Name", "╰┈➤" + newValue, false);
                }

                case "preferred_locale" -> eb.addField("Preferred Locale Set To", "╰┈➤" + newValue, false);

                case "afk_channel_id" ->
                        eb.addField("AFK Channel Changed To", "╰┈➤" + GuildUtils.resolveMentionableChannel(newValue, event), false);

                case "default_message_notifications" ->
                        eb.addField("Default Message Notifications Update", "╰┈➤" + GuildUtils.resolveGuildDefaultMessageNotificationLevel(newValue), false);

                case "afk_timeout" ->
                        eb.addField("AFK Channel Timeout Change", "╰┈➤" + DurationUtils.formatSeconds(newValue), false);

                case "system_channel_id" ->
                        eb.addField("Community Updates Channel Changed To", "╰┈➤" + GuildUtils.resolveMentionableChannel(newValue, event), false);

                case "widget_enabled" ->
                        eb.addField("Widget Enabled", "╰┈➤" + BooleanUtils.formatToYesOrNo(newValue), false);

                case "widget_channel_id" ->
                        eb.addField("Widget Channel Changed To", "╰┈➤" + GuildUtils.resolveMentionableChannel(newValue, event), false);

                case "premium_progress_bar_enabled" ->
                        eb.addField("Server Boost Progress Bar Enabled", "╰┈➤" + BooleanUtils.formatToYesOrNo(newValue), false);

                case "mfa_level" ->
                        eb.addField("MFA Requirement Set To", "╰┈➤" + GuildUtils.resolveGuildModActionMFALevel(newValue), false);

                case "verification_level" ->
                        eb.addField("Verification Level Set To", "╰┈➤" + GuildUtils.resolveGuildVerificationLevel(newValue), false);

                case "owner_id" -> {
                    User oldOwner = ale.getJDA().getUserById(String.valueOf(oldValue));
                    User newOwner = ale.getJDA().getUserById(String.valueOf(newValue));
                    String mentionableOldOwner = (oldOwner != null ? oldOwner.getAsMention() : String.valueOf(oldValue));
                    String mentionableNewOwner = (newOwner != null ? newOwner.getAsMention() : String.valueOf(newValue));
                    eb.addField("Old Owner", "╰┈➤" + mentionableOldOwner, false);
                    eb.addField("New Owner", "╰┈➤" + mentionableNewOwner, false);
                }

                case "public_updates_channel_id" ->
                        eb.addField("Announcements Channel Changed To", "╰┈➤" + GuildUtils.resolveMentionableChannel(newValue, event), false);

                case "rules_channel_id" ->
                        eb.addField("Rules Channel Changed To", "╰┈➤" + GuildUtils.resolveMentionableChannel(newValue, event), false);

                case "system_channel_flags" ->
                        eb.addField("System Channel Flags", "╰┈➤" + GuildUtils.resolveSystemChannelFlags(newValue), false);

                case "explicit_content_filter" ->
                        eb.addField("Explicit Content Filter", "╰┈➤" + GuildUtils.resolveExplicitContentFilterLevel(newValue), false);

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
