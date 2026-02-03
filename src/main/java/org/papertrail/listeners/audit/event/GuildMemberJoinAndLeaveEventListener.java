package org.papertrail.listeners.audit.event;

import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.papertrail.commons.utilities.BooleanFormatter;
import org.papertrail.commons.utilities.DurationFormatter;
import org.papertrail.commons.utilities.EnvConfig;

import java.awt.Color;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Executor;

// this event is not properly logged in the audit logs, hence the usage of JDA's listener is preferred
// this event is logged in the same channel where the audit log events are logged
public class GuildMemberJoinAndLeaveEventListener extends ListenerAdapter {

    private static final AuditLogRegistrationClient client = new AuditLogRegistrationClient(EnvConfig.get("API_URL"));
	private final Executor vThreadPool;

	public GuildMemberJoinAndLeaveEventListener(Executor vThreadPool) {
		this.vThreadPool = vThreadPool;
	}
	
	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

		vThreadPool.execute(()->{
			// guild member join and leave events are mapped to audit log table
            // Call the API and see if the event came from a registered Guild
            Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());
            response.ifPresent(success -> {

                String registeredChannelId = success.getChannelId();

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
                eb.addField("🤖 Bot Account", "╰┈➤"+ BooleanFormatter.formatToYesOrNo(user.isBot()), false);

                eb.setFooter("Member Join Detection");
                eb.setTimestamp(Instant.now());

                MessageEmbed mb = eb.build();

                TextChannel sendingChannel = event.getGuild().getTextChannelById(registeredChannelId);
                if(sendingChannel!=null && sendingChannel.canTalk()) {
                    sendingChannel.sendMessageEmbeds(mb).queue();
                }
            });
		});
	}

	@Override
	public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {

		vThreadPool.execute(() -> {
            // Call the API and see if the event came from a registered Guild
            Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());
            response.ifPresent(success -> {

                String registeredChannelId = success.getChannelId();

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

                TextChannel sendingChannel = event.getGuild().getTextChannelById(registeredChannelId);
                if(sendingChannel!=null && sendingChannel.canTalk()) {
                    sendingChannel.sendMessageEmbeds(mb).queue();
                }
            });

		});
	}

}
