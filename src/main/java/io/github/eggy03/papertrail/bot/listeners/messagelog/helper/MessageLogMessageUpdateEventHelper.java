package io.github.eggy03.papertrail.bot.listeners.messagelog.helper;

import com.google.common.base.Splitter;
import io.github.eggy03.papertrail.sdk.client.MessageLogContentClient;
import io.github.eggy03.papertrail.sdk.entity.MessageLogContentEntity;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@UtilityClass
@Slf4j
public class MessageLogMessageUpdateEventHelper {

    public static void updateMessage(@NonNull MessageUpdateEvent event, @NonNull MessageLogContentClient client, @NonNull String updatedMessageId, @NonNull String channelIdToSendTo) {

        // fetch the old message object from the API
        Optional<MessageLogContentEntity> oldMessageContent = client.retrieveMessage(updatedMessageId);
        oldMessageContent.ifPresent(contentEntity -> {
            // Fetch the old message
            String oldMessage = contentEntity.getMessageContent();
            // fetch the updated message and its author from the event
            String updatedMessage = event.getMessage().getContentDisplay();

            // Ignore events where the message content wasn't edited (e.g., pin, embed resolve, thread creates and updates)
            // This is required since MessageUpdateEvent is triggered in case of pins and embed resolves with no change to content
            if (updatedMessage.equals(oldMessage)) {
                return;
            }

            // Splitting is required because each field in an embed can display only up-to 1024 characters
            // A full embed can display up-to 6000 characters
            List<String> oldMessageSplits = Splitter.fixedLength(1024).splitToList(oldMessage);
            List<String> updatedMessageSplits = Splitter.fixedLength(1024).splitToList(updatedMessage);

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Message Edit Event");
            eb.setDescription("A message sent by " + event.getAuthor().getAsMention() + " has been edited in: " + event.getJumpUrl());
            eb.setColor(Color.YELLOW);

            oldMessageSplits.forEach(split -> eb.addField("Old Message", split, false));
            updatedMessageSplits.forEach(split -> eb.addField("New Message", split, false));

            eb.setFooter(event.getGuild().getName());
            eb.setTimestamp(Instant.now());

            // update the database with the new message
            client.updateMessage(updatedMessageId, updatedMessage, event.getAuthor().getId());

            if (!eb.isValidLength() || eb.isEmpty()) {
                log.warn("Embed is empty or too long (current length: {}).", eb.length());
                return;
            }

            // send the old and updated message to the registered channel
            TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
            if (sendingChannel != null && sendingChannel.canTalk()) {
                sendingChannel.sendMessageEmbeds(eb.build()).queue();
            }
        });
    }
}
