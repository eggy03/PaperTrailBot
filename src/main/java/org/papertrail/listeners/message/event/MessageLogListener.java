package org.papertrail.listeners.message.event;

import com.google.common.base.Splitter;
import io.github.eggy03.papertrail.sdk.client.MessageLogContentClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.MessageLogContentEntity;
import io.github.eggy03.papertrail.sdk.entity.MessageLogRegistrationEntity;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.papertrail.commons.utilities.EnvConfig;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

@Slf4j
public class MessageLogListener extends ListenerAdapter {

    private static final MessageLogRegistrationClient registrationClient = new MessageLogRegistrationClient(EnvConfig.get("API_URL"));
    private static final MessageLogContentClient contentClient = new MessageLogContentClient(EnvConfig.get("API_URL"));

    private final Executor vThreadPool;

	public MessageLogListener(Executor vThreadPool) {
		this.vThreadPool = vThreadPool;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		// if the author is a bot or system, don't log
		if(event.getAuthor().isBot() || event.getAuthor().isSystem()) {
			return;
		}
		// don't register non-textual contents
		if(event.getMessage().getContentRaw().isEmpty()) {
			return;
		}

		vThreadPool.execute(()-> {

            // get the guild id for which the event was fired
            String guildId = event.getGuild().getId();
            // Call the API to see if the guild is registered for Message Logging
            Optional<MessageLogRegistrationEntity> response = registrationClient.getRegisteredGuild(guildId);
            response.ifPresent(success -> {
                String messageId = event.getMessageId();
                String messageContent = event.getMessage().getContentDisplay();
                String authorId = event.getAuthor().getId();

                contentClient.logMessage(messageId, messageContent, authorId);
            });
		});
	}
	
	@Override
	public void onMessageUpdate(MessageUpdateEvent event) {

		if(event.getAuthor().isBot() || event.getAuthor().isSystem()) {
			return;
		}

		vThreadPool.execute(()->{

            // Get the guild id for which the event was fired
            String guildId = event.getGuild().getId();
            // get the message id of the message which was updated
            String messageId = event.getMessageId();
            // Call the API to see if the guild is registered for Message Logging
            Optional<MessageLogRegistrationEntity> response = registrationClient.getRegisteredGuild(guildId);
            response.ifPresent(success -> {

                // retrieve the channel id where the logs must be sent
                String channelIdToSendTo = success.getChannelId();

                // fetch the old message object from the API
                Optional<MessageLogContentEntity> oldMessageContent = contentClient.retrieveMessage(messageId);
                oldMessageContent.ifPresent(contentEntity -> {
                    // Fetch the old message
                    String oldMessage = contentEntity.getMessageContent();
                    // fetch the updated message and its author from the event
                    String updatedMessage = event.getMessage().getContentDisplay();

                    // Ignore events where the message content wasn't edited (e.g., pin, embed resolve, thread creates and updates)
                    // This is required since MessageUpdateEvent is triggered in case of pins and embed resolves with no change to content
                    if(updatedMessage.equals(oldMessage)) {
                        return;
                    }

                    // Splitting is required because each field in an embed can display only up-to 1024 characters
                    // A full embed can display up-to 6000 characters
                    List<String> oldMessageSplits = Splitter.fixedLength(1024).splitToList(oldMessage);
                    List<String> updatedMessageSplits = Splitter.fixedLength(1024).splitToList(updatedMessage);

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("📝 Message Edit Event");
                    eb.setDescription("A message sent by "+event.getAuthor().getAsMention()+" has been edited in: "+event.getJumpUrl());
                    eb.setColor(Color.YELLOW);

                    oldMessageSplits.forEach(split -> eb.addField("Old Message", split, false));
                    updatedMessageSplits.forEach(split -> eb.addField("New Message", split, false));

                    eb.setFooter(event.getGuild().getName());
                    eb.setTimestamp(Instant.now());

                    MessageEmbed mb = eb.build();

                    // send the old and updated message to the registered channel
                    TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
                    if(sendingChannel!=null && sendingChannel.canTalk()) {
                        sendingChannel.sendMessageEmbeds(mb).queue();
                    }

                    // update the database with the new message
                    contentClient.updateMessage(messageId, updatedMessage, event.getAuthor().getId());
                });
            });
		});
	}
	
	@Override
	public void onMessageDelete(@NotNull MessageDeleteEvent event) {

		vThreadPool.execute(()-> {

            // Get the guild id for which the event was fired
            String guildId = event.getGuild().getId();
            // get the message id of the message which was deleted
            String messageId = event.getMessageId();

            // Call the API to see if the guild is registered for Message Logging
            Optional<MessageLogRegistrationEntity> response = registrationClient.getRegisteredGuild(guildId);
            response.ifPresent(success -> {

                // retrieve the channel id where the logs must be sent
                String channelIdToSendTo = success.getChannelId();

                // fetch the old message object from the API
                Optional<MessageLogContentEntity> oldMessageContent = contentClient.retrieveMessage(messageId);
                oldMessageContent.ifPresent(contentEntity -> {

                    // retrieve the stored message and author in the database which was deleted
                    String deletedMessage = contentEntity.getMessageContent();
                    String deletedMessageAuthorId = contentEntity.getAuthorId();

                    User author = event.getJDA().getUserById(deletedMessageAuthorId);
                    String mentionableAuthor = (author !=null ? author.getAsMention() : deletedMessageAuthorId);

                    // Splitting is required because each field in an embed can display only up-to 1024 characters
                    List<String> deletedMessageSplits = Splitter.fixedLength(1024).splitToList(deletedMessage);

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("🗑️ Message Delete Event");
                    eb.setDescription("A message sent by "+mentionableAuthor+" has been deleted from "+ event.getChannel().getAsMention());
                    eb.setColor(Color.RED);

                    deletedMessageSplits.forEach(split-> eb.addField("Deleted Message", split, false));

                    eb.setFooter(event.getGuild().getName());
                    eb.setTimestamp(Instant.now());

                    // send the fetched deleted message to the logging channel
                    MessageEmbed mb = eb.build();

                    TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
                    if(sendingChannel!=null && sendingChannel.canTalk()) {
                        sendingChannel.sendMessageEmbeds(mb).queue();
                    }

                    // delete the message from the database
                    contentClient.deleteMessage(messageId);
                });
            });
		});
	}
}
