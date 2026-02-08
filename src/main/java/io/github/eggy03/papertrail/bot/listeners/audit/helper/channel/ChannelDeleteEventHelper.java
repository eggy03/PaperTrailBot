package io.github.eggy03.papertrail.bot.listeners.audit.helper.channel;

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
public class ChannelDeleteEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        GuildChannel targetChannel = ale.getJDA().getGuildChannelById(ale.getTargetIdLong());

        String mentionableTargetChannel = (targetChannel != null ? targetChannel.getAsMention() : ale.getTargetId()); // this will return only the ID cause the channel with the ID has been deleted

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Channel Delete Event");

        eb.setDescription("ℹ️ The following channel was deleted by: " + mentionableExecutor);
        eb.setColor(Color.RED);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "name" -> eb.addField("Name", "╰┈➤" + oldValue, false);

                case "type" -> eb.addField("🗨️ Type", "╰┈➤" + TypeResolver.channelTypeResolver(oldValue), false);

                case "user_limit", "rate_limit_per_user", "default_thread_rate_limit_per_user", "nsfw",
                     "permission_overwrites", "video_quality_mode", "flags", "bitrate", "rtc_region", "template",
                     "available_tags", "topic", "default_auto_archive_duration", "default_reaction_emoji" -> {
                    // not strictly needed to be displayed
                    // not all cases are available for all types of channels
                }

                default -> {
                    eb.addField(changeKey, "OLD_VALUE: " + oldValue, false);
                    eb.addField(changeKey, "NEW_VALUE: " + newValue, false);
                }
            }
        });

        eb.addField("🆔 Deleted Channel ID", "╰┈➤" + mentionableTargetChannel, false);

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }
}
