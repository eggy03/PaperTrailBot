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
import org.papertrail.commons.utilities.MessageEncryption;

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
                String encryptedMessage = MessageEncryption.encrypt(event.getMessage().getContentRaw());
                String authorId = event.getAuthor().getId();

                contentClient.logMessage(messageId, encryptedMessage, authorId);
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
                oldMessageContent.ifPresent(content -> {
                    // Decrypt the fetched message
                    String decryptedMessage = MessageEncryption.decrypt(content.getMessageContent());
                    // fetch the updated message and its author from the event
                    String updatedMessage = event.getMessage().getContentRaw();

                    // Ignore events where the message content wasn't edited (e.g., pin, embed resolve, thread creates and updates)
                    // This is required since MessageUpdateEvent is triggered in case of pins and embed resolves with no change to content
                    if(updatedMessage.equals(decryptedMessage)) {
                        return;
                    }

                    // Splitting is required because each field in an embed can display only up-to 1024 characters
                    // A full embed can display up-to 6000 characters
                    assert decryptedMessage != null; //TODO when removing encryption, remove this as well
                    List<String> decryptedMessageSplits = Splitter.fixedLength(1024).splitToList(decryptedMessage);
                    List<String> updatedMessageSplits = Splitter.fixedLength(1024).splitToList(updatedMessage);

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("📝 Message Edit Event");
                    eb.setDescription("A message sent by "+event.getAuthor().getAsMention()+" has been edited in: "+event.getJumpUrl());
                    eb.setColor(Color.YELLOW);

                    decryptedMessageSplits.forEach(split -> eb.addField("Old Message", split, false)); // get only the message and not the author
                    updatedMessageSplits.forEach(split -> eb.addField("New Message", split, false));

                    eb.setFooter(event.getGuild().getName());
                    eb.setTimestamp(Instant.now());
                    // update the database with the new message
                    contentClient.updateMessage(messageId, MessageEncryption.encrypt(updatedMessage), event.getAuthor().getId());
                    // the reason this is above the send queue is that in case where the user did not give sufficient permissions to
                    // the bot, the error responses wouldn't block the update of the message in the database.

                    // fetch the channel id from the database
                    // this channel is where the logs will be sent to
                    // wrap the embed and send
                    MessageEmbed mb = eb.build();

                    TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
                    if(sendingChannel!=null && sendingChannel.canTalk()) {
                        sendingChannel.sendMessageEmbeds(mb).queue();
                    }
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
                oldMessageContent.ifPresent(content -> {

                    // retrieve the stored message and author in the database which was deleted
                    String deletedMessage = MessageEncryption.decrypt(content.getMessageContent());
                    String deletedMessageAuthorId = content.getAuthorId();

                    User author = event.getJDA().getUserById(deletedMessageAuthorId);
                    String mentionableAuthor = (author !=null ? author.getAsMention() : deletedMessageAuthorId);

                    // Splitting is required because each field in an embed can display only up-to 1024 characters
                    assert deletedMessage != null; //TODO when removing encryption, remove this as well
                    List<String> deletedMessageSplits = Splitter.fixedLength(1024).splitToList(deletedMessage);

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("🗑️ Message Delete Event");
                    eb.setDescription("A message sent by "+mentionableAuthor+" has been deleted");
                    eb.setColor(Color.RED);

                    deletedMessageSplits.forEach(split-> eb.addField("Deleted Message", split, false));

                    eb.setFooter(event.getGuild().getName());
                    eb.setTimestamp(Instant.now());

                    // delete the message from the database
                    contentClient.deleteMessage(messageId);
                    // the reason this is above the send queue is that, in case where the user did not give sufficient permissions to
                    // the bot, (such as no send message permissions) the exceptions wouldn't block the deletion in the database.

                    // send the fetched deleted message to the logging channel
                    MessageEmbed mb = eb.build();

                    TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
                    if(sendingChannel!=null && sendingChannel.canTalk()) {
                        sendingChannel.sendMessageEmbeds(mb).queue();
                    }
                });
            });
		});
	}
}
