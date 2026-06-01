package io.github.eggy03.papertrail.bot.handlers.guild;

import io.github.eggy03.papertrail.bot.utils.BooleanUtils;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.time.Instant;

@ApplicationScoped
@Slf4j
public final class GuildPollEventHandler {

    private final @NonNull AuditLogRegistrationClient client;

    @Inject
    public GuildPollEventHandler(@NonNull AuditLogRegistrationClient client) {
        this.client = client;
    }

    @NonNull
    private String getRegisteredGuildChannel(@NonNull String guildId) {
        return client.getRegisteredGuild(guildId)
                .map(AuditLogRegistrationEntity::getChannelId).orElse(StringUtils.EMPTY);

    }

    private void performChecksThenBuildAndSendEmbed(@NonNull MessageReceivedEvent event, @NonNull EmbedBuilder embedBuilder, @NonNull String channelIdToSendTo) {
        if (!embedBuilder.isValidLength() || embedBuilder.isEmpty()) {
            log.warn("Embed is empty or too long (current length: {}).", embedBuilder.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    public void handlePollCreationEvent(@NonNull MessageReceivedEvent event) {
        // check if message has a poll
        MessagePoll messagePoll = event.getMessage().getPoll();
        if (messagePoll == null)
            return;

        String channelIdToSendTo = getRegisteredGuildChannel(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Poll Creation Event");
        eb.setColor(Color.PINK);
        eb.setDescription(MarkdownUtil.quoteBlock("Poll Created By: " + event.getAuthor().getAsMention() + "\nTarget Channel: " + event.getChannel().getAsMention()));

        eb.addField(MarkdownUtil.underline("Question"), messagePoll.getQuestion().getText(), false);
        eb.addField(MarkdownUtil.underline("Answers"), getMessagePollAnswers(messagePoll), false);
        eb.addField(MarkdownUtil.underline("Poll Expiry Time"), getPollExpiryTime(messagePoll), false);
        eb.addField(MarkdownUtil.underline("Accepts Multiple Answers"), BooleanUtils.formatToYesOrNo(messagePoll.isMultiAnswer()), false);

        eb.setFooter(event.getGuild().getName());
        eb.setTimestamp(Instant.now());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);

    }

    @NotNull
    private String getMessagePollAnswers(@NonNull MessagePoll messagePoll) {

        StringBuilder answers = new StringBuilder();
        messagePoll.getAnswers().forEach(answer -> answers
                .append("*Answer: * ")
                .append(answer.getText())
                .append(" *Emoji: * ")
                .append(answer.getEmoji() == null ? "N/A" : answer.getEmoji().getFormatted())
                .append(System.lineSeparator())
        );

        return answers.toString().trim();
    }

    @NotNull
    private String getPollExpiryTime(@NonNull MessagePoll messagePoll) {
        return messagePoll.getTimeExpiresAt() != null ?
                "<t:" + messagePoll.getTimeExpiresAt().toEpochSecond() + ":f>" :
                "Never Expires";
    }
}
