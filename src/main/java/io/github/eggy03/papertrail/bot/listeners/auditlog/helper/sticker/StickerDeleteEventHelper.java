package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.sticker;

import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.sticker.utils.StickerUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;

@UtilityClass
@Slf4j
public class StickerDeleteEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        GuildSticker sticker = event.getGuild().getStickerById(ale.getTargetId());
        String mentionableSticker = (sticker != null ? sticker.getName() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Sticker Delete Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Sticker Deleted By: " + mentionableExecutor + "\nDeleted Sticker ID: " + mentionableSticker));
        eb.setColor(Color.RED);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "format_type", "type", "asset", "available", "guild_id" -> {
                    // skip
                }
                case "id" -> {
                    eb.addField(MarkdownUtil.underline("Sticker ID"), "╰┈➤" + oldValue, false);
                    eb.addField(MarkdownUtil.underline("Sticker Link"), "╰┈➤" + StickerUtils.resolveStickerUrl(event, oldValue), false);
                }
                case "tags" ->
                        eb.addField(MarkdownUtil.underline("Related Emoji"), "╰┈➤" + StickerUtils.resolveRelatedEmoji(event, oldValue), false);
                case "description" -> eb.addField(MarkdownUtil.underline("Description"), "╰┈➤" + oldValue, false);
                case "name" -> eb.addField(MarkdownUtil.underline("Sticker Name"), "╰┈➤" + oldValue, false);

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
