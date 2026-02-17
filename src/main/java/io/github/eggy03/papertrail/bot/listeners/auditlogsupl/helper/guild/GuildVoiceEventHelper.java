package io.github.eggy03.papertrail.bot.listeners.auditlogsupl.helper.guild;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

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
        eb.setTitle("🔊 Voice Activity Log");

        if (left == null && joined != null) {
            // User has joined a vc
            eb.setDescription("A Member has joined a voice channel");
            eb.setColor(Color.GREEN);
            eb.addField("✅ Member Joined", "╰┈➤" + member.getAsMention() + " joined the voice channel " + joined.getAsMention(), false);
        }

        if (left != null && joined != null) {
            // Moved from one channel to another
            eb.setDescription("A Member has switched voice channels");
            eb.setColor(Color.YELLOW);
            eb.addField("🔄 Member Switched Channels", "╰┈➤" + member.getAsMention() + " joined the switched from channel " + left.getAsMention() + " to " + joined.getAsMention(), false);
        }

        if (left != null && joined == null) {
            // User disconnected voluntarily (or was disconnected by a moderator)
            eb.setDescription("A Member has left a voice channel");
            eb.setColor(Color.RED);
            eb.addField("❌ Member Left A Voice Channel", "╰┈➤" + member.getAsMention() + " left the voice channel " + left.getAsMention(), false);
        }

        eb.setFooter("Voice Activity Detection");
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
