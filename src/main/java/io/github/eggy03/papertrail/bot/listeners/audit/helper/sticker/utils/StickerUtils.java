package io.github.eggy03.papertrail.bot.listeners.audit.helper.sticker.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
@Slf4j
public class StickerUtils {

    public static final String FALLBACK_STRING = "N/A";

    @NotNull
    public static String resolveStickerUrl(@NonNull GenericGuildEvent event, @Nullable Object stickerIdObject) {

        if (stickerIdObject == null) {
            log.debug("stickerIdObject is null (guild={})", event.getGuild().getId());
            return FALLBACK_STRING;
        }

        String stickerIdStr = String.valueOf(stickerIdObject);
        GuildSticker sticker = event.getGuild().getStickerById(String.valueOf(stickerIdObject));

        if (sticker == null) {
            log.debug("sticker not found (stickerIdObject={}, guild={})", stickerIdStr, event.getGuild().getId());
            return stickerIdStr;
        }

        return sticker.getIconUrl();

    }

    // emojiIdObject does NOT give you snowflake IDs in case of Unicode emojis
    // it only gives u IDs in case of custom emojis
    // any values caught by NumberFormatException are Unicode emojis and can be returned raw
    @NotNull
    public static String resolveRelatedEmoji(@NonNull GenericGuildEvent event, @Nullable Object emojiIdObject) {

        if (emojiIdObject == null) {
            log.debug("emojiIdObject is null (guild={})", event.getGuild().getId());
            return FALLBACK_STRING;
        }

        String emojiIdString = String.valueOf(emojiIdObject);

        try {
            long emojiIdLong = Long.parseLong(emojiIdString);
            Emoji emoji = event.getGuild().getEmojiById(emojiIdLong);

            if (emoji == null) {
                log.debug("custom emoji not found (emojiIdLong={}, guild={})", emojiIdLong, event.getGuild().getId());
                return emojiIdString;
            }
            return emoji.getFormatted();

        } catch (NumberFormatException e) {
            log.debug("value is not numeric, treating as Unicode (value={}, guild={})", emojiIdString, event.getGuild().getId());
            return emojiIdString;
        }

    }
}
