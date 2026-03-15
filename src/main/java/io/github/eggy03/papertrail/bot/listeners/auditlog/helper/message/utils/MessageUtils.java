package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.message.utils;

import io.github.eggy03.papertrail.bot.commons.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class MessageUtils {

    private static final String FALLBACK_STRING = "N/A";

    @NotNull
    @Blocking
    public static String resolveTextMessageFromId(@Nullable Object channelId, @Nullable Object messageId, @NonNull GenericGuildEvent event) {

        Long channelIdLong = NumberParseUtils.parseLong(channelId);
        Long messageIdLong = NumberParseUtils.parseLong(messageId);

        if (channelIdLong == null || messageIdLong == null)
            return FALLBACK_STRING;

        TextChannel textChannel = event.getGuild().getTextChannelById(channelIdLong);

        if (textChannel == null)
            return FALLBACK_STRING;

        // Blocking REST Action
        Message message = textChannel.retrieveMessageById(messageIdLong).complete();

        if (message == null)
            return FALLBACK_STRING;

        String textContent = message.getContentDisplay();

        // for blank texts, this could indicate that it has a sticker or an embed such as img, video or GIF
        // in which case, just provide the messageID
        if (textContent.isBlank())
            return "Message ID: " + messageIdLong;

        return textContent;
    }
}
