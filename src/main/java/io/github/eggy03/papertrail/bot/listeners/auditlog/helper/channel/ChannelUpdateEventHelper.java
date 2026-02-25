package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.channel;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.commons.utils.DurationUtils;
import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.channel.utils.ChannelUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;

@UtilityClass
@Slf4j
public class ChannelUpdateEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());
        String targetChannelMention = (targetChannel != null ? targetChannel.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Channel Update Event");

        eb.setDescription(MarkdownUtil.quoteBlock("Channel Updated By: " + mentionableExecutor + "\nTarget Channel: " + targetChannelMention));
        eb.setColor(Color.YELLOW);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "user_limit" -> {
                    eb.addField(MarkdownUtil.underline("Old User Limit"), "╰┈➤" + ChannelUtils.resolveVoiceChannelUserLimit(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New User Limit"), "╰┈➤" + ChannelUtils.resolveVoiceChannelUserLimit(newValue), true);
                    eb.addBlankField(true);
                }

                case "rate_limit_per_user" -> {
                    eb.addField(MarkdownUtil.underline("Old Slow mode Value"), "╰┈➤" + DurationUtils.formatSeconds(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Slow mode Value"), "╰┈➤" + DurationUtils.formatSeconds(newValue), true);
                    eb.addBlankField(true);
                }

                case "default_thread_rate_limit_per_user" -> {
                    eb.addField(MarkdownUtil.underline("Old Thread Slow mode Value"), "╰┈➤" + DurationUtils.formatSeconds(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Thread Slow mode Value"), "╰┈➤" + DurationUtils.formatSeconds(newValue), true);
                    eb.addBlankField(true);
                }

                case "nsfw" -> {
                    eb.addField(MarkdownUtil.underline("Was NSFW"), "╰┈➤" + BooleanUtils.formatToYesOrNo(oldValue), true);
                    eb.addField(MarkdownUtil.underline("Is NSFW"), "╰┈➤" + BooleanUtils.formatToYesOrNo(newValue), true);
                    eb.addBlankField(true);
                }

                case "video_quality_mode" -> {
                    eb.addField(MarkdownUtil.underline("Old Video Quality Mode"), "╰┈➤" + ChannelUtils.resolveVoiceChannelVideoQuality(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Video Quality Mode"), "╰┈➤" + ChannelUtils.resolveVoiceChannelVideoQuality(newValue), true);
                    eb.addBlankField(true);
                }

                case "name" -> {
                    eb.addField(MarkdownUtil.underline("Old Channel Name"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Channel Name"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }

                case "bitrate" -> {
                    eb.addField(MarkdownUtil.underline("Old Voice Channel Bitrate"), "╰┈➤" + ChannelUtils.resolveVoiceChannelBitrate(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Voice Channel Bitrate"), "╰┈➤" + ChannelUtils.resolveVoiceChannelBitrate(newValue), true);
                    eb.addBlankField(true);
                }

                case "rtc_region" -> {
                    eb.addField(MarkdownUtil.underline("Old Region"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Region"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }

                case "topic" -> {
                    eb.addField(MarkdownUtil.underline("Old Topic"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New topic"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }

                case "default_auto_archive_duration" -> {
                    eb.addField(MarkdownUtil.underline("Old Hide After Inactivity Timer"), "╰┈➤" + DurationUtils.formatMinutes(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Hide After Inactivity Timer"), "╰┈➤" + DurationUtils.formatMinutes(newValue), true);
                    eb.addBlankField(true);
                }

                case "type" -> {
                    eb.addField(MarkdownUtil.underline("Old Type"), ChannelUtils.resolveChannelType(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Type"), ChannelUtils.resolveChannelType(newValue), true);
                    eb.addBlankField(true);
                }

                case "available_tags" ->
                        eb.addField(MarkdownUtil.underline("Tags"), "╰┈➤The channel's tags were updated", false);
                case "default_reaction_emoji" ->
                        eb.addField(MarkdownUtil.underline("Reaction Emoji"), "╰┈➤Default reaction emoji was updated", false);
                case "flags" ->
                        eb.addField(MarkdownUtil.underline("Flags"), "╰┈➤The channel's flags were updated", false);

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
