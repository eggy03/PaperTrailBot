package org.papertrail.listeners.guildlisteners;

import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.papertrail.sdk.client.AuditLogClient;
import org.papertrail.sdk.model.result.ApiResult;
import org.papertrail.sdk.model.AuditLogResponse;
import org.papertrail.sdk.model.ErrorResponse;

public class ServerBoostListener extends ListenerAdapter {

	private final Executor vThreadPool;

	public ServerBoostListener( Executor vThreadPool) {
		this.vThreadPool = vThreadPool;
	}
	
	@Override
	public void onGuildMemberUpdateBoostTime(@NotNull GuildMemberUpdateBoostTimeEvent event) {

		vThreadPool.execute(()->{
			// server boost logs are mapped to audit log table
            // Call the API and see if the event came from a registered Guild
            ApiResult<AuditLogResponse, ErrorResponse> guildCheck = AuditLogClient.getRegisteredGuild(event.getGuild().getId());

            if(guildCheck.isError()){
                return;
            }
			String registeredChannelId= guildCheck.success().channelId();

			Member member = event.getMember();
			Guild guild = event.getGuild();

			String mentionableMember = member.getAsMention();

			OffsetDateTime newBoostTime = event.getNewTimeBoosted(); // Will be null if the member stopped boosting

			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("🚀 Server Boost Event ");
			eb.setThumbnail(guild.getIconUrl());

			if (newBoostTime != null) {
				eb.setDescription("🎉 **" + guild.getName() + "** has been boosted!");
				eb.setColor(Color.PINK);
				eb.addField("🔋 Booster Gained", "╰┈➤"+mentionableMember+" has started boosting your server", false);
				eb.addField("📈 Total Boosts In The Server", "╰┈➤"+guild.getBoostCount(), false);
				eb.addField("🎖️ Current Boost Tier", "╰┈➤"+ guild.getBoostTier(), false);
			} else {
				eb.setDescription("⚠️ **" + guild.getName() + "** has lost a boost.");
				eb.setColor(Color.GRAY);
				eb.addField("🪫 Booster Lost", "╰┈➤"+mentionableMember+" has removed their boost from your server", false);
				eb.addField("📉 Remaining Boosts In The Server", "╰┈➤"+guild.getBoostCount(), false);
				eb.addField("🎖️ Current Boost Tier", "╰┈➤"+ guild.getBoostTier(), false);
				eb.addField("Notice", "Boosts remain active for a period even after a member stops boosting, so the server's boost count doesn't update immediately.", false);
			}

			eb.setFooter("Server Boost Detection");
			eb.setTimestamp(Instant.now());

			MessageEmbed mb = eb.build();
			Objects.requireNonNull(event.getGuild().getTextChannelById(registeredChannelId)).sendMessageEmbeds(mb).queue();
		});
	}
}
