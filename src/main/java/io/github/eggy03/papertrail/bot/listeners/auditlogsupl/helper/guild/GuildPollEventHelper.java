package io.github.eggy03.papertrail.bot.listeners.auditlogsupl.helper.guild;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.time.Instant;

@UtilityClass
@Slf4j
public class GuildPollEventHelper {

    public static void format(@NonNull MessageReceivedEvent event, @NonNull MessagePoll messagePoll, @NonNull String channelIdToSendTo) {

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

        if (!eb.isValidLength() || eb.isEmpty()) {
            log.warn("Embed is empty or too long (current length: {}).", eb.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }

    @NotNull
    private static String getMessagePollAnswers(@NonNull MessagePoll messagePoll) {

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
    private static String getPollExpiryTime(@NonNull MessagePoll messagePoll) {
        return messagePoll.getTimeExpiresAt() != null ?
                "<t:" + messagePoll.getTimeExpiresAt().toEpochSecond() + ":f>" :
                "Never Expires";
    }
}
