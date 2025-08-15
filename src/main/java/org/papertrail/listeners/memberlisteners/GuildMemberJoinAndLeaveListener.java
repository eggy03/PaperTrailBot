package org.papertrail.listeners.memberlisteners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.papertrail.sdk.call.AuditLogSetupCall;
import org.papertrail.sdk.response.ApiResponse;
import org.papertrail.sdk.response.AuditLogResponseObject;
import org.papertrail.sdk.response.ErrorResponseObject;
import org.papertrail.utilities.DurationFormatter;

import java.awt.Color;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Executor;

public class GuildMemberJoinAndLeaveListener extends ListenerAdapter {

	private final Executor vThreadPool;

	public GuildMemberJoinAndLeaveListener (Executor vThreadPool) {

		this.vThreadPool = vThreadPool;
	}
	
	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

		vThreadPool.execute(()->{
			// guild member join and leave events are mapped to audit log table
            // Call the API and see if the event came from a registered Guild
            ApiResponse<AuditLogResponseObject, ErrorResponseObject> guildCheck = AuditLogSetupCall.getRegisteredGuild(event.getGuild().getId());

            if(guildCheck.isError()){
                return;
            }
			String registeredChannelId = guildCheck.success().channelId();

			Guild guild = event.getGuild();
			User user = event.getUser();

			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("🛬 Member Join Event");
			eb.setDescription("A Member has joined "+guild.getName());
			eb.setColor(Color.GREEN);

			eb.addField("🏷️ Member Name", "╰┈➤"+user.getName(), false);
			eb.setThumbnail(user.getEffectiveAvatarUrl());
			eb.addField("ℹ️ Member Mention", "╰┈➤"+user.getAsMention(), false);
			eb.addField("🆔 Member ID", "╰┈➤"+user.getId(), false);
			eb.addField("📅 Account Created", "╰┈➤"+DurationFormatter.isoToLocalTimeCounter(user.getTimeCreated()), false);
			eb.addField("🤖 Is Application ?", "╰┈➤"+((user.isBot()) ? "✅" : "❌"), false);
			eb.setFooter("Member Join Detection");
			eb.setTimestamp(Instant.now());

			MessageEmbed mb = eb.build();

			Objects.requireNonNull(event.getGuild().getTextChannelById(registeredChannelId)).sendMessageEmbeds(mb).queue();
		});
	}

	@Override
	public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {

		vThreadPool.execute(() -> {
            // Call the API and see if the event came from a registered Guild
            ApiResponse<AuditLogResponseObject, ErrorResponseObject> guildCheck = AuditLogSetupCall.getRegisteredGuild(event.getGuild().getId());

            if(guildCheck.isError()){
                return;
            }
			String registeredChannelId = guildCheck.success().channelId();

			Guild guild = event.getGuild();
			User user = event.getUser();
            Member member = event.getMember();

            String memberJoinDate = "Member Not Cached";
            boolean memberJoinDateTrustable = false;
            if(member!=null){
                memberJoinDate = "<t:" +member.getTimeJoined().toEpochSecond()+ ":f>";
                memberJoinDateTrustable = member.hasTimeJoined();
            }

			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("🛫 Member Leave Event");
			eb.setDescription("A Member has left "+guild.getName());
			eb.setColor(Color.RED);

			eb.addField("🏷️ Member Name", "╰┈➤"+user.getName(), false);
			eb.setThumbnail(user.getEffectiveAvatarUrl());
			eb.addField("🆔 Member ID", "╰┈➤"+user.getId(), false);
            eb.addField("⌛ Member Joined The Server On","╰┈➤"+memberJoinDate, false);
            eb.addField("⌛ Member Join Date Accurate?", memberJoinDateTrustable ? "✅" : "❌" , false);

			eb.setFooter("Join timestamp may fall back to guild creation time if not provided by Discord during lazy loading of members. " +
                    "Accuracy of the timestamp can be determined by the extra field provided.");
			eb.setTimestamp(Instant.now());

			MessageEmbed mb = eb.build();

			Objects.requireNonNull(event.getGuild().getTextChannelById(registeredChannelId)).sendMessageEmbeds(mb).queue();
		});
	}

}
