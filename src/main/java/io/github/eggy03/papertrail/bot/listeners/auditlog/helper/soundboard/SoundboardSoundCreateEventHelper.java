package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.soundboard;

import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.soundboard.utils.SoundboardUtils;
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
public class SoundboardSoundCreateEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Soundboard Sound Create Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Sound Item Added By: " + mentionableExecutor));
        eb.setColor(Color.GREEN);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "user_id", "sound_id", "id", "guild_id", "available" -> {
                    // skip
                }
                case "volume" ->
                        eb.addField(MarkdownUtil.underline("Volume"), "╰┈➤" + SoundboardUtils.resolveVolumePercentage(newValue), false);

                case "emoji_name" -> eb.addField(MarkdownUtil.underline("Related Emoji"), "╰┈➤" + newValue, false);

                case "emoji_id" -> eb.addField(MarkdownUtil.underline("Related Emoji ID"), "╰┈➤" + newValue, false);

                case "name" -> eb.addField(MarkdownUtil.underline("Sound Item Name"), "╰┈➤" + newValue, false);

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
