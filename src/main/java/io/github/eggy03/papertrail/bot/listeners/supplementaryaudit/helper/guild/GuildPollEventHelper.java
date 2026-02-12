package io.github.eggy03.papertrail.bot.listeners.supplementaryaudit.helper.guild;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.time.Instant;

@UtilityClass
public class GuildPollEventHelper {

    public static void format(@NonNull MessageReceivedEvent event, @NonNull MessagePoll messagePoll, @NonNull String channelIdToSendTo) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("\uD83D\uDCCA Poll Creation Log");
        eb.setColor(Color.PINK);
        eb.setDescription("A Poll has been created");

        eb.addField("Poll Creator", event.getAuthor().getAsMention(), false);
        eb.addField("Question", messagePoll.getQuestion().getText(), false);
        eb.addField("Answers", getMessagePollAnswers(messagePoll), false);
        eb.addField("Poll Expiry Time", getPollExpiryTime(messagePoll), false);
        eb.addField("Accepts Multiple Answers", BooleanUtils.formatToYesOrNo(messagePoll.isMultiAnswer()), false);
        eb.addField("Channel", event.getChannel().getAsMention(), false);

        eb.setFooter("Poll Activity Detection");
        eb.setTimestamp(Instant.now());

        MessageEmbed mb = eb.build();

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
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
