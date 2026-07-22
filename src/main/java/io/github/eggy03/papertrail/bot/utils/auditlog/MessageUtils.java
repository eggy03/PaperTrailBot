package io.github.eggy03.papertrail.bot.utils.auditlog;

import io.github.eggy03.papertrail.bot.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.Nullable;

@UtilityClass
@Slf4j
public final class MessageUtils {

    private static final String FALLBACK_STRING = "N/A";

    @NonNull
    @Blocking
    public static String resolveMessageJumpUrlFromId(@Nullable Object channelId, @Nullable Object messageId, @NonNull GenericGuildEvent event) {

        Long channelIdLong = NumberParseUtils.parseLong(channelId);
        Long messageIdLong = NumberParseUtils.parseLong(messageId);
        if (channelIdLong == null || messageIdLong == null)
            return FALLBACK_STRING;

        TextChannel textChannel = event.getGuild().getTextChannelById(channelIdLong);
        if (textChannel == null)
            return FALLBACK_STRING;

        // Blocking REST Action
        // read retrieveMessageById doc for try catch explanation
        try {
            Message message = textChannel.retrieveMessageById(messageIdLong).complete();
            return message.getJumpUrl();
        } catch (InsufficientPermissionException | ErrorResponseException e) {
            log.debug("Error while retrieving message by ID", e);
            return "Unable to resolve the message jump URL due to insufficient permissions or access.";
        }

    }
}
