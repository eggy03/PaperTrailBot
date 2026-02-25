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
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;

@UtilityClass
@Slf4j
public class ThreadUpdateEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        ThreadChannel targetThread = event.getGuild().getThreadChannelById(ale.getTargetId());
        String mentionableTargetThread = (targetThread != null ? targetThread.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Thread Update Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Thread Updated By: " + mentionableExecutor + "\nTarget Thread: " + mentionableTargetThread));
        eb.setColor(Color.YELLOW);


        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "locked" -> {
                    eb.addField(MarkdownUtil.underline("Was Locked"), "╰┈➤" + BooleanUtils.formatToYesOrNo(oldValue), true);
                    eb.addField(MarkdownUtil.underline("Is Locked"), "╰┈➤" + BooleanUtils.formatToYesOrNo(newValue), true);
                    eb.addBlankField(true);
                }
                case "auto_archive_duration" -> {
                    eb.addField(MarkdownUtil.underline("Old Auto Archive Duration"), "╰┈➤" + ThreadUtils.resolveAutoArchiveDuration(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Auto Archive Duration"), "╰┈➤" + ThreadUtils.resolveAutoArchiveDuration(newValue), true);
                    eb.addBlankField(true);
                }
                case "rate_limit_per_user" -> {
                    eb.addField(MarkdownUtil.underline("Old Slow Mode Limit"), "╰┈➤" + DurationUtils.formatSeconds(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Slow Mode Limit"), "╰┈➤" + DurationUtils.formatSeconds(newValue), true);
                    eb.addBlankField(true);
                }
                case "type" -> {
                    eb.addField(MarkdownUtil.underline("Old Thread Type"), "╰┈➤" + ChannelUtils.resolveChannelType(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Thread Type"), "╰┈➤" + ChannelUtils.resolveChannelType(newValue), true);
                    eb.addBlankField(true);
                }
                case "archived" -> {
                    eb.addField(MarkdownUtil.underline("Old Archive Status"), "╰┈➤" + BooleanUtils.formatToEnabledOrDisabled(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Archive Status"), "╰┈➤" + BooleanUtils.formatToEnabledOrDisabled(newValue), true);
                    eb.addBlankField(true);
                }
                case "flags" -> eb.addField(MarkdownUtil.underline("Flags"), "╰┈➤Thread flags were updated", false);

                case "invitable" -> {
                    eb.addField(MarkdownUtil.underline("Was Invitable"), "╰┈➤" + BooleanUtils.formatToYesOrNo(oldValue), true);
                    eb.addField(MarkdownUtil.underline("Is Invitable"), "╰┈➤" + BooleanUtils.formatToYesOrNo(newValue), true);
                    eb.addBlankField(true);
                }
                case "name" -> {
                    eb.addField(MarkdownUtil.underline("Old Thread Name"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Thread Name"), "╰┈➤" + newValue, true);
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
