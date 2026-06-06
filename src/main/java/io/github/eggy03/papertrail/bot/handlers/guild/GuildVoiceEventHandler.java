package io.github.eggy03.papertrail.bot.handlers.guild;

import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;
import java.time.Instant;

@ApplicationScoped
@Slf4j
public final class GuildVoiceEventHandler {

    private final @NonNull AuditLogRegistrationClient client;

    @Inject
    public GuildVoiceEventHandler(@NonNull AuditLogRegistrationClient client) {
        this.client = client;
    }

    @NonNull
    private String getRegisteredChannelId(@NonNull String guildId) {
        return client.getRegisteredGuild(guildId)
                .map(AuditLogRegistrationEntity::getChannelId).orElse(StringUtils.EMPTY);

    }

    private void performChecksThenBuildAndSendEmbed(@NonNull GenericGuildEvent event, @NonNull EmbedBuilder embedBuilder, @NonNull String channelIdToSendTo) {
        if (!embedBuilder.isValidLength() || embedBuilder.isEmpty()) {
            log.warn("Embed is empty or too long (current length: {}).", embedBuilder.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    public void handleVoiceUpdateEvent(@NonNull GuildVoiceUpdateEvent event) {

        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        Member member = event.getMember();
        AudioChannel left = event.getOldValue(); // can be null if user joined for first time
        AudioChannel joined = event.getNewValue(); // can be null if user left

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Voice Activity Sub-Entry");

        if (left == null && joined != null) {
            // User has joined a vc
            eb.setDescription("A Member Has Joined A Voice Channel");
            eb.setColor(Color.GREEN);
            eb.addField(
                    MarkdownUtil.underline("Connection Event Metadata"),
                    MarkdownUtil.quoteBlock("Connected Member: " + member.getAsMention() + "\nJoined Channel: " + joined.getAsMention()),
                    false)
            ;
        }

        if (left != null && joined != null) {
            // Moved from one channel to another
            eb.setDescription("A Member Has Switched Voice Channels");
            eb.setColor(Color.YELLOW);
            eb.addField(
                    MarkdownUtil.underline("Switch Event Metadata"),
                    MarkdownUtil.quoteBlock("Switched Member: " + member.getAsMention() + "\nLeft Channel: " + left.getAsMention() + "\nJoined Channel: " + joined.getAsMention()),
                    false);
        }

        if (left != null && joined == null) {
            // User disconnected voluntarily (or was disconnected by a moderator)
            eb.setDescription("A Member Has Left A Voice Channel");
            eb.setColor(Color.RED);
            eb.addField(
                    MarkdownUtil.underline("Disconnection Event Metadata"),
                    MarkdownUtil.quoteBlock("Disconnected Member: " + member.getAsMention() + "\nLeft Channel: " + left.getAsMention()),
                    false
            );
        }

        eb.setFooter(event.getGuild().getName());
        eb.setTimestamp(Instant.now());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }
}
