package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.thread;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.commons.utils.DurationUtils;
import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.channel.utils.ChannelUtils;
import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.thread.utils.ThreadUtils;
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
public class ThreadDeleteEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Thread Delete Event");
        eb.setDescription("ℹ️ A thread has been deleted by: " + mentionableExecutor);
        eb.setColor(Color.RED);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "locked" -> eb.addField("Locked", "╰┈➤" + BooleanUtils.formatToYesOrNo(oldValue), false);

                case "auto_archive_duration" ->
                        eb.addField("Auto Archive Duration", "╰┈➤" + ThreadUtils.resolveAutoArchiveDuration(oldValue), false);

                case "rate_limit_per_user" ->
                        eb.addField("Slow Mode Limit", "╰┈➤" + DurationUtils.formatSeconds(oldValue), false);

                case "type" -> eb.addField("Thread Type", "╰┈➤" + ChannelUtils.resolveChannelType(oldValue), false);

                case "archived" -> eb.addField("Archived", "╰┈➤" + BooleanUtils.formatToYesOrNo(oldValue), false);

                case "flags" -> {
                    // skip
                }

                case "invitable" -> eb.addField("Invitable", "╰┈➤" + BooleanUtils.formatToYesOrNo(oldValue), false);

                case "name" -> eb.addField("Thread Name", "╰┈➤" + oldValue, false);

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
