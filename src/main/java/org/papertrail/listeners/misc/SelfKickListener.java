package org.papertrail.listeners.misc;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.papertrail.commons.sdk.client.AuditLogClient;
import org.papertrail.commons.sdk.client.MessageLogClient;

import java.util.concurrent.Executor;

/*
 * This class will have methods that unregister the log channels from the database after the bot has been kicked
 */
public class SelfKickListener extends ListenerAdapter {

	private final Executor vThreadPool;

	public SelfKickListener(Executor vThreadPool) {
		this.vThreadPool = vThreadPool;
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event) {

		Guild leftGuild = event.getGuild();
		vThreadPool.execute(()->{
            AuditLogClient.deleteRegisteredGuild(leftGuild.getId());
            MessageLogClient.deleteRegisteredGuild(leftGuild.getId());
		});
	}
}
