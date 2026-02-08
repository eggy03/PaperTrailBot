package io.github.eggy03.papertrail.bot.listeners.audit.helper.channel;

import io.github.eggy03.papertrail.bot.commons.utilities.BooleanFormatter;
import io.github.eggy03.papertrail.bot.commons.utilities.DurationFormatter;
import io.github.eggy03.papertrail.bot.commons.utilities.TypeResolver;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
public class ChannelUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());
        String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Channel Update Event");

        eb.setDescription("ℹ️ The following channel was updated by: "+mentionableExecutor);
        eb.setColor(Color.YELLOW);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);
        eb.addBlankField(true);

        ale.getChanges().forEach((changeKey, changeValue)-> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "user_limit" -> {
                    eb.addField("Old User Limit", "╰┈➤"+ TypeResolver.formatNumberOrUnlimited(oldValue), true);
                    eb.addField("New User Limit", "╰┈➤"+TypeResolver.formatNumberOrUnlimited(newValue), true);
                    eb.addBlankField(true);
                }

                case "rate_limit_per_user" -> {
                    eb.addField("Old Slow mode Value", "╰┈➤"+ DurationFormatter.formatSeconds(oldValue), true);
                    eb.addField("New Slow mode Value", "╰┈➤"+DurationFormatter.formatSeconds(newValue), true);
                    eb.addBlankField(true);
                }

                case "default_thread_rate_limit_per_user" -> {
                    eb.addField("Old Thread Slow mode Value", "╰┈➤"+ DurationFormatter.formatSeconds(oldValue), true);
                    eb.addField("New Thread Slow mode Value", "╰┈➤"+DurationFormatter.formatSeconds(newValue), true);
                    eb.addBlankField(true);
                }

                case "nsfw" -> {
                    eb.addField("Was NSFW", "╰┈➤"+ BooleanFormatter.formatToYesOrNo(oldValue), true);
                    eb.addField("Is NSFW", "╰┈➤"+BooleanFormatter.formatToYesOrNo(newValue), true);
                    eb.addBlankField(true);
                }

                case "video_quality_mode" -> {
                    eb.addField("Old Video Quality Mode", "╰┈➤"+TypeResolver.videoQualityModeResolver(oldValue), true);
                    eb.addField("New Video Quality Mode", "╰┈➤"+TypeResolver.videoQualityModeResolver(newValue), true);
                    eb.addBlankField(true);
                }

                case "name" -> {
                    eb.addField("Old Channel Name", "╰┈➤"+oldValue, true);
                    eb.addField("New Channel Name", "╰┈➤"+newValue, true);
                    eb.addBlankField(true);
                }

                case "bitrate" -> {
                    eb.addField("Old Voice Channel Bitrate", "╰┈➤"+TypeResolver.voiceChannelBitrateResolver(oldValue), true);
                    eb.addField("New Voice Channel Bitrate", "╰┈➤"+TypeResolver.voiceChannelBitrateResolver(newValue), true);
                    eb.addBlankField(true);
                }

                case "rtc_region" -> {
                    eb.addField("Old Region", "╰┈➤"+oldValue, true);
                    eb.addField("New Region", "╰┈➤"+newValue, true);
                    eb.addBlankField(true);
                }

                case "topic" -> {
                    eb.addField("Old Topic", "╰┈➤"+oldValue, true);
                    eb.addField("New topic", "╰┈➤"+newValue, true);
                    eb.addBlankField(true);
                }

                case "default_auto_archive_duration" -> {
                    eb.addField("Old Hide After Inactivity Timer", "╰┈➤"+DurationFormatter.formatMinutes(oldValue), true);
                    eb.addField("New Hide After Inactivity Timer", "╰┈➤"+DurationFormatter.formatMinutes(newValue), true);
                    eb.addBlankField(true);
                }

                case "available_tags" -> eb.addField("Tags", "╰┈➤The channel's tags were updated", false);
                case "default_reaction_emoji" -> eb.addField("Reaction Emoji", "╰┈➤Default reaction emoji was updated", false);

                default -> {
                    eb.addField(changeKey, "OLD_VALUE: "+oldValue, true);
                    eb.addField(changeKey, "NEW_VALUE: "+newValue, true);
                    eb.addBlankField(true);
                }

            }
        });

        // mention the channel that got updated, id can be exposed via ALE's TargetID
        eb.addField("Target Channel", "╰┈➤"+mentionableTargetChannel, false);

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }
}
