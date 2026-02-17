package io.github.eggy03.papertrail.bot.listeners.messagelog.helper;

import io.github.eggy03.papertrail.sdk.client.MessageLogContentClient;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@UtilityClass
public class MessageLogMessageReceivedEventHelper {

    public static void saveMessage(@NonNull MessageReceivedEvent event, @NonNull MessageLogContentClient client) {

        String messageId = event.getMessageId();
        String messageContent = event.getMessage().getContentDisplay();
        String authorId = event.getAuthor().getId();

        client.logMessage(messageId, messageContent, authorId);
    }
}
