package io.github.eggy03.papertrail.bot.listeners.audit.helper.channel;

import io.github.eggy03.papertrail.bot.commons.utilities.BooleanFormatter;
import io.github.eggy03.papertrail.bot.commons.utilities.DurationFormatter;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.utils.ChannelUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
@Slf4j
public class ChannelCreateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        GuildChannel targetChannel = ale.getGuild().getGuildChannelById(ale.getTargetId());
        String mentionableTargetChannel = (targetChannel !=null ? targetChannel.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Channel Create Event");

        eb.setDescription("ℹ️ The following channel was created by: "+mentionableExecutor);
        eb.setColor(Color.GREEN);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        ale.getChanges().forEach((changeKey, changeValue)-> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "user_limit" -> eb.addField("User Limit", "╰┈➤"+ ChannelUtils.resolveVoiceChannelUserLimit(newValue), false);

                case "rate_limit_per_user" -> eb.addField("Slow Mode", "╰┈➤"+ DurationFormatter.formatSeconds(newValue), false);

                case "type" -> eb.addField("Channel Type", "╰┈➤"+ ChannelUtils.resolveChannelType(newValue), false);

                case "nsfw" -> eb.addField("Is NSFW", "╰┈➤"+ BooleanFormatter.formatToYesOrNo(newValue), false);

                case "name" -> {
                    eb.addField("Channel Name", "╰┈➤"+newValue, false);
                    eb.addField("Channel Mention", "╰┈➤"+mentionableTargetChannel, true);
                }

                case "bitrate" -> eb.addField("Voice Channel Bitrate", "╰┈➤"+ ChannelUtils.resolveVoiceChannelBitrate(newValue), false);

                case "permission_overwrites", "flags", "template", "available_tags" -> {
                    // the first two are for all types of channels and may stay empty during creation events
                    // the second two are forum only cases which stay empty during creation
                }

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });


        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();
        if(!mb.isSendable()){
            log.warn("An embed is either empty or has exceed the max length for characters, with current length: {}", eb.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
