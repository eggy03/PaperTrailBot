package io.github.eggy03.papertrail.bot.listeners.messagelog.event;

import io.github.eggy03.papertrail.bot.commons.utils.EnvConfig;
import io.github.eggy03.papertrail.bot.listeners.messagelog.helper.MessageLogMessageDeleteEventHelper;
import io.github.eggy03.papertrail.bot.listeners.messagelog.helper.MessageLogMessageReceivedEventHelper;
import io.github.eggy03.papertrail.bot.listeners.messagelog.helper.MessageLogMessageUpdateEventHelper;
import io.github.eggy03.papertrail.sdk.client.MessageLogContentClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.MessageLogRegistrationEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;
import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
public class MessageLogListener extends ListenerAdapter {

    @NonNull
    private static final MessageLogRegistrationClient registrationClient = new MessageLogRegistrationClient(EnvConfig.get("API_URL"));
    @NonNull
    private static final MessageLogContentClient contentClient = new MessageLogContentClient(EnvConfig.get("API_URL"));
    @NonNull
    private final Executor vThreadPool;

    @Override
    public void onMessageReceived(@NonNull MessageReceivedEvent event) {

        // if the author is a bot or system, don't log
        if (event.getAuthor().isBot() || event.getAuthor().isSystem()) {
            return;
        }
        // don't register non-textual contents
        if (event.getMessage().getContentRaw().isEmpty()) {
            return;
        }

        vThreadPool.execute(() -> {
            // get the guild id for which the event was fired
            String guildId = event.getGuild().getId();
            // Call the API to see if the guild is registered for Message Logging
            Optional<MessageLogRegistrationEntity> response = registrationClient.getRegisteredGuild(guildId);
            response.ifPresent(success ->
                    MessageLogMessageReceivedEventHelper.saveMessage(event, contentClient)
            );
        });
    }

    @Override
    public void onMessageUpdate(@NonNull MessageUpdateEvent event) {

        if (event.getAuthor().isBot() || event.getAuthor().isSystem()) {
            return;
        }

        vThreadPool.execute(() -> {
            // Get the guild id for which the event was fired
            String guildId = event.getGuild().getId();
            // Call the API to see if the guild is registered for Message Logging
            Optional<MessageLogRegistrationEntity> response = registrationClient.getRegisteredGuild(guildId);
            response.ifPresent(success -> {

                // get the message id of the message which was updated
                String messageId = event.getMessageId();
                // retrieve the channel id where the logs must be sent
                String channelIdToSendTo = success.getChannelId();
                // update message
                MessageLogMessageUpdateEventHelper.updateMessage(event, contentClient, messageId, channelIdToSendTo);
            });
        });
    }

    @Override
    public void onMessageDelete(@NonNull MessageDeleteEvent event) {

        vThreadPool.execute(() -> {

            // Get the guild id for which the event was fired
            String guildId = event.getGuild().getId();

            // Call the API to see if the guild is registered for Message Logging
            Optional<MessageLogRegistrationEntity> response = registrationClient.getRegisteredGuild(guildId);
            response.ifPresent(success -> {

                // get the message id of the message which was deleted
                String messageId = event.getMessageId();
                // retrieve the channel id where the logs must be sent
                String channelIdToSendTo = success.getChannelId();
                // delete message
                MessageLogMessageDeleteEventHelper.deleteMessage(event, contentClient, messageId, channelIdToSendTo);

            });
        });
    }
}
