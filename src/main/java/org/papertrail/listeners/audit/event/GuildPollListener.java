package org.papertrail.listeners.audit.event;

import io.vavr.control.Either;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jspecify.annotations.NonNull;
import org.papertrail.commons.sdk.client.AuditLogClient;
import org.papertrail.commons.sdk.model.AuditLogObject;
import org.papertrail.commons.sdk.model.ErrorObject;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class GuildPollListener extends ListenerAdapter {

    private final Executor vThreadPool;

    public GuildPollListener(Executor vThreadPool) {
        this.vThreadPool = vThreadPool;
    }

    @Override
    public void onMessageReceived(@NonNull MessageReceivedEvent event) {

        if(!event.isFromGuild())
            return;

        MessagePoll messagePoll = event.getMessage().getPoll();
        if(messagePoll==null)
            return;

        vThreadPool.execute(()-> {

            // guild poll events are mapped to audit log table
            // Call the API and see if the event came from a registered Guild
            Either<ErrorObject, AuditLogObject> response = AuditLogClient.getRegisteredGuild(event.getGuild().getId());

            response.peek(success -> {

                String registeredChannelId = success.channelId();

                StringBuilder answers = new StringBuilder();
                List<MessagePoll.Answer> answerList = messagePoll.getAnswers();
                answerList.forEach(answer -> answers
                        .append("*Answer: * ")
                        .append(answer.getText())
                        .append(" *Emoji: * ")
                        .append(answer.getEmoji()==null ? "N/A" : answer.getEmoji().getFormatted())
                        .append(System.lineSeparator())
                );

                String expiryTime = messagePoll.getTimeExpiresAt()!=null ?
                        "<t:" +messagePoll.getTimeExpiresAt().toEpochSecond()+ ":f>" :
                        "N/A";


                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("\uD83D\uDCCA Poll Creation Log");
                eb.setColor(Color.PINK);
                eb.setDescription("A Poll has been created");
                eb.addField("Poll Creator", event.getAuthor().getAsMention(), false);
                eb.addField("Question", messagePoll.getQuestion().getText(), false);
                eb.addField("Answers", answers.toString(), false);
                eb.addField("Poll Expiry Time", expiryTime, false);
                eb.addField("Accepts Multiple Answers", messagePoll.isMultiAnswer() ? "Yes" : "No", false);
                eb.addField("Channel", event.getChannel().getAsMention(), false);

                eb.setFooter("Poll Activity Detection");
                eb.setTimestamp(Instant.now());

                MessageEmbed mb = eb.build();

                TextChannel sendingChannel = event.getGuild().getTextChannelById(registeredChannelId);
                if(sendingChannel!=null && sendingChannel.canTalk()) {
                    sendingChannel.sendMessageEmbeds(mb).queue();
                }

            });
        });
    }
}
