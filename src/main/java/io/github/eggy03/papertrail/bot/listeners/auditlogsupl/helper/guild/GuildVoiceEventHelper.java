package io.github.eggy03.papertrail.bot.listeners.auditlogsupl.helper.guild;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;
import java.time.Instant;

@UtilityClass
@Slf4j
public class GuildVoiceEventHelper {

    public static void format(@NonNull GuildVoiceUpdateEvent event, @NonNull String channelIdToSendTo) {

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

        if (!eb.isValidLength() || eb.isEmpty()) {
            log.warn("Embed is empty or too long (current length: {}).", eb.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }
}
