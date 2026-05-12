package io.github.eggy03.papertrail.bot.listeners.misc;

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
    private final AuditLogRegistrationClient auditLogRegistrationClient;

    @NonNull
    private final MessageLogRegistrationClient messageLogRegistrationClient;

    @NonNull
    private final Executor vThreadPool;

    @Override
    public void onGuildLeave(@NonNull GuildLeaveEvent event) {

        Guild leftGuild = event.getGuild();
        vThreadPool.execute(() -> {
            auditLogRegistrationClient.deleteRegisteredGuild(leftGuild.getId());
            messageLogRegistrationClient.deleteRegisteredGuild(leftGuild.getId());
        });
    }
}
