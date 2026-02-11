package io.github.eggy03.papertrail.bot.listeners.audit.helper.sticker.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class StickerUtils {

    public static final String FALLBACK_STRING = "N/A";

    @NotNull
    public static String resolveStickerUrl(@NonNull GenericGuildEvent event, @Nullable Object stickerId) {
        if (stickerId == null) return FALLBACK_STRING;

        GuildSticker sticker = event.getGuild().getStickerById(String.valueOf(stickerId));
        if (sticker == null) return String.valueOf(stickerId);

        return sticker.getIconUrl();

    }

    // emojiIdLong does NOT give you snowflake IDs in case of Unicode emojis
    // it only gives u IDs in case of custom emojis
    // any values caught by NumberFormatException are Unicode emojis and can be returned raw
    @NotNull
    public static String resolveRelatedEmoji(@NonNull GenericGuildEvent event, @Nullable Object emojiIdLong) {

        if (emojiIdLong == null) return FALLBACK_STRING;

        try {
            long emojiId = Long.parseLong(String.valueOf(emojiIdLong));
            Emoji emoji = event.getGuild().getEmojiById(emojiId);
            if (emoji == null)
                return String.valueOf(emojiId);

            return emoji.getFormatted();
        } catch (NumberFormatException e) {
            return String.valueOf(emojiIdLong);
        }

    }
}
