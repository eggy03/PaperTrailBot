package io.github.eggy03.papertrail.bot.listeners.messagelog.helper;

import com.google.common.base.Splitter;
import io.github.eggy03.papertrail.sdk.client.MessageLogContentClient;
import io.github.eggy03.papertrail.sdk.entity.MessageLogContentEntity;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@UtilityClass
@Slf4j
public class MessageLogMessageDeleteEventHelper {

    public static void deleteMessage(@NonNull MessageDeleteEvent event, @NonNull MessageLogContentClient client, @NonNull String deletedMessageId, @NonNull String channelIdToSendTo) {
        // fetch the old message object from the API
        Optional<MessageLogContentEntity> oldMessageContent = client.retrieveMessage(deletedMessageId);
        oldMessageContent.ifPresent(contentEntity -> {

            // retrieve the stored message and author in the database which was deleted
            String deletedMessage = contentEntity.getMessageContent();
            String deletedMessageAuthorId = contentEntity.getAuthorId();

            User author = event.getJDA().getUserById(deletedMessageAuthorId);
            String mentionableAuthor = (author != null ? author.getAsMention() : deletedMessageAuthorId);

            // Splitting is required because each field in an embed can display only up-to 1024 characters
            List<String> deletedMessageSplits = Splitter.fixedLength(1024).splitToList(deletedMessage);

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("🗑️ Message Delete Event");
            eb.setDescription("A message sent by " + mentionableAuthor + " has been deleted from " + event.getChannel().getAsMention());
            eb.setColor(Color.RED);

            deletedMessageSplits.forEach(split -> eb.addField("Deleted Message", split, false));

            eb.setFooter(event.getGuild().getName());
            eb.setTimestamp(Instant.now());

            // send the fetched deleted message to the logging channel
            MessageEmbed mb = eb.build();
            if (!mb.isSendable()) {
                log.warn("Embed is empty or too long (current length: {}).", eb.length());
                return;
            }

            TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
            if (sendingChannel != null && sendingChannel.canTalk()) {
                sendingChannel.sendMessageEmbeds(mb).queue();
            }

            // delete the message from the database
            client.deleteMessage(deletedMessageId);
        });
    }
}
