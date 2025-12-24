package org.papertrail.listeners.audit.event;

import io.vavr.control.Either;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.papertrail.commons.sdk.client.AuditLogClient;
import org.papertrail.commons.sdk.model.AuditLogObject;
import org.papertrail.commons.sdk.model.ErrorObject;
import org.papertrail.commons.utilities.DurationFormatter;

import java.awt.Color;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Executor;

// this event is not properly logged in the audit logs, hence the usage of JDA's listener is preferred
// this event is logged in the same channel where the audit log events are logged
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
            Either<ErrorObject, AuditLogObject> response = AuditLogClient.getRegisteredGuild(event.getGuild().getId());
            response.peek(success -> {

                String registeredChannelId = success.channelId();

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
		});
	}

	@Override
	public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {

		vThreadPool.execute(() -> {
            // Call the API and see if the event came from a registered Guild
            Either<ErrorObject, AuditLogObject> response = AuditLogClient.getRegisteredGuild(event.getGuild().getId());
            response.peek(success -> {

                String registeredChannelId = success.channelId();

                Guild guild = event.getGuild();
                User user = event.getUser();
                Member member = event.getMember();

                String memberJoinDate = "Member Not Cached";
                boolean memberJoinDateTrustable = false;
                if(member!=null){
                    memberJoinDate = "<t:" +member.getTimeJoined().toEpochSecond()+ ":f>";
                    memberJoinDateTrustable = member.hasTimeJoined();
                }
                String memberLeaveDate = "<t:" +Instant.now().getEpochSecond()+ ":f>";

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("🛫 Member Leave Event");
                eb.setDescription("A Member has left "+guild.getName());
                eb.setColor(Color.RED);

                eb.addField("🏷️ Member Name", "╰┈➤"+user.getName(), false);
                eb.setThumbnail(user.getEffectiveAvatarUrl());
                eb.addField("🆔 Member ID", "╰┈➤"+user.getId(), false);
                eb.addField("⌛ Member Joined The Server On","╰┈➤"+memberJoinDate, false);
                eb.addField("⌛ Member Left The Server On","╰┈➤"+memberLeaveDate, false);
                eb.addField("⌛ Member Join Date Validity", memberJoinDateTrustable ? "╰┈➤Valid" : "╰┈➤Invalid" , false);

                eb.setFooter("If the member was loaded via lazy loading, join date will be identical to the guild creation date.");
                eb.setTimestamp(Instant.now());

                MessageEmbed mb = eb.build();

                Objects.requireNonNull(event.getGuild().getTextChannelById(registeredChannelId)).sendMessageEmbeds(mb).queue();
            });

		});
	}

}
