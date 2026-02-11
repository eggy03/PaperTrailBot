package io.github.eggy03.papertrail.bot.listeners.audit.helper.sticker.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class StickerUtils {

    @NotNull
    public static String resolveStickerUrl(@NonNull GenericGuildEvent event, @Nullable Object stickerId) {
        if (stickerId == null) return "N/A";

        GuildSticker sticker = event.getGuild().getStickerById(String.valueOf(stickerId));
        if (sticker == null) return "N/A";

        return sticker.getIconUrl();

    }
}
