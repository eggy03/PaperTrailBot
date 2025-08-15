package org.papertrail.listeners.voicelisteners;

import java.awt.Color;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.papertrail.sdk.call.AuditLogSetupCall;
import org.papertrail.sdk.response.ApiResponse;
import org.papertrail.sdk.response.AuditLogResponseObject;
import org.papertrail.sdk.response.ErrorResponseObject;

public class GuildVoiceListener extends ListenerAdapter {

	private final Executor vThreadPool;

	public GuildVoiceListener(Executor vThreadPool) {
		this.vThreadPool = vThreadPool;
	}
	
	@Override
	public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {

		vThreadPool.execute(()->{

			// guild voice events are mapped to audit log table
            // Call the API and see if the event came from a registered Guild
            ApiResponse<AuditLogResponseObject, ErrorResponseObject> guildCheck = AuditLogSetupCall.getRegisteredGuild(event.getGuild().getId());

            if(guildCheck.isError()){
                return ;
            }
			String registeredChannelId = guildCheck.success().channelId();

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

			Objects.requireNonNull(event.getGuild().getTextChannelById(registeredChannelId)).sendMessageEmbeds(mb).queue();
		});
	}
}
