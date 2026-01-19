package org.papertrail.listeners.message.event;

import com.google.common.base.Splitter;
import com.google.common.util.concurrent.Striped;
import io.vavr.control.Either;
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
import org.jspecify.annotations.NonNull;
import org.papertrail.commons.sdk.client.MessageContentLogClient;
import org.papertrail.commons.sdk.client.MessageLogClient;
import org.papertrail.commons.sdk.model.ErrorObject;
import org.papertrail.commons.sdk.model.MessageContentObject;
import org.papertrail.commons.sdk.model.MessageLogObject;
import org.papertrail.commons.utilities.MessageEncryption;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

@Slf4j
public class MessageLogListener extends ListenerAdapter {

	private final Executor vThreadPool;
	private final Striped<@NonNull Lock> messageLocks = Striped.lock(8192);
	private final AtomicLong activeLockCount = new AtomicLong(0);

	public MessageLogListener(Executor vThreadPool) {
		this.vThreadPool = vThreadPool;
	}

	private void withMessageLock (String messageId, Runnable task) {
		Lock lock = messageLocks.get(messageId);
		lock.lock();
		log.debug("Lock acquired for message id: {} . Active lock count: {}", messageId, activeLockCount.incrementAndGet());
		try{
			task.run();
		} finally {
			lock.unlock();
			log.debug("Lock released for message id: {} . Active lock count: {}", messageId, activeLockCount.decrementAndGet());
		}
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
            Either<ErrorObject, MessageLogObject> response = MessageLogClient.getRegisteredGuild(guildId);
            response.peek(success -> {
                String messageId = event.getMessageId();
                String encryptedMessage = MessageEncryption.encrypt(event.getMessage().getContentRaw());
                String authorId = event.getAuthor().getId();

                withMessageLock(messageId, ()-> MessageContentLogClient.logMessage(messageId, encryptedMessage, authorId));
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
            Either<ErrorObject, MessageLogObject> response = MessageLogClient.getRegisteredGuild(guildId);
            response.peek(success -> {

                // retrieve the channel id where the logs must be sent
                String channelIdToSendTo = success.channelId();

                // fetch the old message object from the API
                Either<ErrorObject, MessageContentObject>  updatedMessageObject = MessageContentLogClient.retrieveMessage(messageId);
                updatedMessageObject.peek(content -> {
                    // Decrypt the fetched message
                    String decryptedMessage = MessageEncryption.decrypt(content.messageContent());
                    // fetch the updated message and its author from the event
                    String updatedMessage = event.getMessage().getContentRaw();

                    // Ignore events where the message content wasn't edited (e.g., pin, embed resolve, thread creates and updates)
                    // This is required since MessageUpdateEvent is triggered in case of pins and embed resolves with no change to content
                    if(updatedMessage.equals(decryptedMessage)) {
                        return;
                    }

                    // Splitting is required because each field in an embed can display only up-to 1024 characters
                    assert decryptedMessage != null;
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
                    withMessageLock(messageId, ()->
                            MessageContentLogClient.updateMessage(
                                    messageId, MessageEncryption.encrypt(updatedMessage), event.getAuthor().getId()
                            )
                    );
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
            Either<ErrorObject, MessageLogObject> response = MessageLogClient.getRegisteredGuild(guildId);
            response.peek(success -> {

                // retrieve the channel id where the logs must be sent
                String channelIdToSendTo = success.channelId();

                // fetch the old message object from the API
                Either<ErrorObject, MessageContentObject> deletedMessageObject = MessageContentLogClient.retrieveMessage(messageId);
                deletedMessageObject.peek(content -> {

                    // retrieve the stored message and author in the database which was deleted
                    String deletedMessage = MessageEncryption.decrypt(content.messageContent());
                    String deletedMessageAuthorId = content.authorId();

                    User author = event.getJDA().getUserById(deletedMessageAuthorId);
                    String mentionableAuthor = (author !=null ? author.getAsMention() : deletedMessageAuthorId);

                    // Splitting is required because each field in an embed can display only up-to 1024 characters
                    assert deletedMessage != null;
                    List<String> deletedMessageSplits = Splitter.fixedLength(1024).splitToList(deletedMessage);

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("🗑️ Message Delete Event");
                    eb.setDescription("A message sent by "+mentionableAuthor+" has been deleted");
                    eb.setColor(Color.RED);

                    deletedMessageSplits.forEach(split-> eb.addField("Deleted Message", split, false));

                    eb.setFooter(event.getGuild().getName());
                    eb.setTimestamp(Instant.now());

                    // delete the message from the database
                    withMessageLock(messageId, ()-> MessageContentLogClient.deleteMessage(messageId));
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
