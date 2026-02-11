package io.github.eggy03.papertrail.bot.listeners.misc;

import io.github.eggy03.papertrail.bot.commons.utils.EnvConfig;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.Executor;

/*
 * This class will have methods that unregister the log channels from the database after the bot has been kicked
 */
@RequiredArgsConstructor
public class SelfKickListener extends ListenerAdapter {

    @NonNull
    private static final AuditLogRegistrationClient alClient = new AuditLogRegistrationClient(EnvConfig.get("API_URL"));

    @NonNull
    private static final MessageLogRegistrationClient mlClient = new MessageLogRegistrationClient(EnvConfig.get("API_URL"));

    private final Executor vThreadPool;

    @Override
    public void onGuildLeave(@NonNull GuildLeaveEvent event) {

        Guild leftGuild = event.getGuild();
        vThreadPool.execute(() -> {
            alClient.deleteRegisteredGuild(leftGuild.getId());
            mlClient.deleteRegisteredGuild(leftGuild.getId());
        });
    }
}
