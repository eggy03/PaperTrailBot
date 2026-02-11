package io.github.eggy03.papertrail.bot.listeners.audit.event.guild;

import io.github.eggy03.papertrail.bot.commons.utils.EnvConfig;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Executor;

// this event is not properly logged in the audit logs, hence the usage of JDA's listener is preferred
// this event is logged in the same channel where the audit log events are logged
@RequiredArgsConstructor
public class GuildVoiceEventListener extends ListenerAdapter {

    @NonNull
    private static final AuditLogRegistrationClient client = new AuditLogRegistrationClient(EnvConfig.get("API_URL"));
	private final Executor vThreadPool;

	@Override
	public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {

		vThreadPool.execute(()->{

			// guild voice events are mapped to audit log table
            // Call the API and see if the event came from a registered Guild
            Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());
            response.ifPresent(success -> {

                String registeredChannelId = success.getChannelId();

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("🔊 Voice Activity Log");

                Member member = event.getMember();
                AudioChannel left = event.getOldValue(); // can be null if user joined for first time
                AudioChannel joined = event.getNewValue(); // can be null if user left

                if(left==null && joined!=null) {
                    // User has joined a vc
                    eb.setDescription("A Member has joined a voice channel");
                    eb.setColor(Color.GREEN);
                    eb.addField("✅ Member Joined", "╰┈➤"+member.getAsMention()+" joined the voice channel "+joined.getAsMention(), false);
                }

                if (left != null && joined != null) {
                    // Moved from one channel to another
                    eb.setDescription("A Member has switched voice channels");
                    eb.setColor(Color.YELLOW);
                    eb.addField("🔄 Member Switched Channels", "╰┈➤"+member.getAsMention()+" joined the switched from channel "+left.getAsMention()+ " to "+joined.getAsMention(), false);
                }

                if (left!=null && joined==null) {
                    // User disconnected voluntarily (or was disconnected by a moderator)
                    eb.setDescription("A Member has left a voice channel");
                    eb.setColor(Color.RED);
                    eb.addField("❌ Member Left A Voice Channel", "╰┈➤"+member.getAsMention()+" left the voice channel "+left.getAsMention(), false);
                }

                eb.setFooter("Voice Activity Detection");
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
