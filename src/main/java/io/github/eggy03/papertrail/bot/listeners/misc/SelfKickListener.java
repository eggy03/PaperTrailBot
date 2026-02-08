package io.github.eggy03.papertrail.bot.listeners.misc;

import io.github.eggy03.papertrail.bot.commons.utilities.EnvConfig;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

/*
 * This class will have methods that unregister the log channels from the database after the bot has been kicked
 */
public class SelfKickListener extends ListenerAdapter {

	private static final AuditLogRegistrationClient alClient = new AuditLogRegistrationClient(EnvConfig.get("API_URL"));
	private static final MessageLogRegistrationClient mlClient = new MessageLogRegistrationClient(EnvConfig.get("API_URL"));

	private final Executor vThreadPool;

	public SelfKickListener(Executor vThreadPool) {
		this.vThreadPool = vThreadPool;
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event) {

		Guild leftGuild = event.getGuild();
		vThreadPool.execute(()->{
			alClient.deleteRegisteredGuild(leftGuild.getId());
			mlClient.deleteRegisteredGuild(leftGuild.getId());
		});
	}
}
