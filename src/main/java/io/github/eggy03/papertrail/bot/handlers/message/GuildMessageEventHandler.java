package io.github.eggy03.papertrail.bot.handlers.message;

import com.google.common.base.Splitter;
import io.github.eggy03.papertrail.sdk.client.MessageLogContentClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.MessageLogContentEntity;
import io.github.eggy03.papertrail.sdk.entity.MessageLogRegistrationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class GuildMessageEventHandler {

    private final @NonNull MessageLogRegistrationClient registrationClient;
    private final @NonNull MessageLogContentClient contentClient;

    @Inject
    public GuildMessageEventHandler(@NonNull MessageLogRegistrationClient registrationClient, @NonNull MessageLogContentClient contentClient) {
        this.registrationClient = registrationClient;
        this.contentClient = contentClient;
    }

    private boolean isGuildRegistered(@NonNull String guildId) {
        return registrationClient.getRegisteredGuild(guildId).isPresent();
    }

    @NonNull
    private String getRegisteredGuildChannel(@NonNull String guildId) {
        return registrationClient.getRegisteredGuild(guildId)
                .map(MessageLogRegistrationEntity::getChannelId).orElse(StringUtils.EMPTY);

    }

    private void performChecksThenBuildAndSendEmbed(@NonNull GenericMessageEvent event, @NonNull EmbedBuilder embedBuilder, @NonNull String channelIdToSendTo) {
        if (!embedBuilder.isValidLength() || embedBuilder.isEmpty()) {
            log.warn("Message Embed is empty or too long (current length: {}).", embedBuilder.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    public void handleMessageReceivedEvent(@NonNull MessageReceivedEvent event) {
        if (!isGuildRegistered(event.getGuild().getId()))
            return;

        String messageId = event.getMessageId();
        String messageContent = event.getMessage().getContentDisplay();
        String authorId = event.getAuthor().getId();

        contentClient.logMessage(messageId, messageContent, authorId);
    }

    public void handleMessageUpdateEvent(@NonNull MessageUpdateEvent event) {
        String channelIdToSendTo = getRegisteredGuildChannel(event.getGuild().getId());
        if (channelIdToSendTo.isBlank())
            return;

        // fetch the old message object from the API
        Optional<MessageLogContentEntity> oldMessageContentOptional = contentClient.retrieveMessage(event.getMessageId());
        if (oldMessageContentOptional.isEmpty())
            return;

        MessageLogContentEntity oldMessageContentEntity = oldMessageContentOptional.get();
        // Fetch the old message
        String oldMessage = oldMessageContentEntity.getMessageContent();
        // fetch the updated message and its author from the event
        String updatedMessage = event.getMessage().getContentDisplay();
        String updatedMessageAuthor = event.getAuthor().getAsMention();

        // Ignore events where the message content wasn't edited (e.g., pin, embed resolve, thread creates and updates)
        // This is required since MessageUpdateEvent is triggered in case of pins and embed resolves with no change to content
        if (updatedMessage.equals(oldMessage))
            return;

        // Splitting is required because each field in an embed can display only up-to 1024 characters
        // A full embed can display up-to 6000 characters
        List<String> oldMessageSplits = Splitter.fixedLength(1000).splitToList(oldMessage);
        List<String> updatedMessageSplits = Splitter.fixedLength(1000).splitToList(updatedMessage);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Message Edit Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Author: " + updatedMessageAuthor + "\n" + "Channel: " + event.getChannel().getAsMention()));
        eb.setColor(Color.YELLOW);

        oldMessageSplits.forEach(split -> eb.addField(MarkdownUtil.underline("Old Message"), MarkdownUtil.codeblock(split), false));
        updatedMessageSplits.forEach(split -> eb.addField(MarkdownUtil.underline("New Message"), MarkdownUtil.codeblock(split), false));

        eb.setFooter(event.getGuild().getName());
        eb.setTimestamp(Instant.now());

        // update the database with the new message
        contentClient.updateMessage(event.getMessageId(), updatedMessage, event.getAuthor().getId());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    public void handleMessageDeleteEvent(@NonNull MessageDeleteEvent event) {
        String channelIdToSendTo = getRegisteredGuildChannel(event.getGuild().getId());
        if (channelIdToSendTo.isBlank())
            return;

        // fetch the message object from the API which was just deleted
        Optional<MessageLogContentEntity> deletedMessageContentOptional = contentClient.retrieveMessage(event.getMessageId());
        if (deletedMessageContentOptional.isEmpty())
            return;

        MessageLogContentEntity deletedMessageContentEntity = deletedMessageContentOptional.get();
        // Fetch the deleted message and it's author id
        String deletedMessage = deletedMessageContentEntity.getMessageContent();
        String deletedMessageAuthorId = deletedMessageContentEntity.getAuthorId();

        User author = event.getJDA().getUserById(deletedMessageAuthorId);
        String mentionableAuthor = (author != null ? author.getAsMention() : deletedMessageAuthorId);

        // Splitting is required because each field in an embed can display only up-to 1024 characters
        List<String> deletedMessageSplits = Splitter.fixedLength(1000).splitToList(deletedMessage);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Message Delete Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Author: " + mentionableAuthor + "\n" + "Channel: " + event.getChannel().getAsMention()));
        eb.setColor(Color.RED);

        deletedMessageSplits.forEach(split -> eb.addField(MarkdownUtil.underline("Deleted Message"), MarkdownUtil.codeblock(split), false));

        eb.setFooter(event.getGuild().getName());
        eb.setTimestamp(Instant.now());

        // delete the message from the database
        contentClient.deleteMessage(event.getMessageId());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }
}
