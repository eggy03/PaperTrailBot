package io.github.eggy03.papertrail.bot.listeners.misc;

import io.github.eggy03.papertrail.bot.qualifiers.VirtualThreadFactory;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadFactory;

/*
 * This class will have methods that unregister the log channels from the database after the bot has been kicked
 */
@Singleton
public final class SelfKickListener extends ListenerAdapter {

    @NonNull
    private final AuditLogRegistrationClient auditLogRegistrationClient;

    @NonNull
    private final MessageLogRegistrationClient messageLogRegistrationClient;

    private final @NonNull
    @VirtualThreadFactory ThreadFactory virtualThreadFactory;

    @Inject
    public SelfKickListener(@NonNull AuditLogRegistrationClient auditLogRegistrationClient, @NonNull MessageLogRegistrationClient messageLogRegistrationClient, @NonNull @VirtualThreadFactory ThreadFactory virtualThreadFactory) {
        this.auditLogRegistrationClient = auditLogRegistrationClient;
        this.messageLogRegistrationClient = messageLogRegistrationClient;
        this.virtualThreadFactory = virtualThreadFactory;
    }

    @Override
    public void onGuildLeave(@NonNull GuildLeaveEvent event) {
        Guild leftGuild = event.getGuild();

        virtualThreadFactory.newThread(() -> {
            auditLogRegistrationClient.deleteRegisteredGuild(leftGuild.getId());
            messageLogRegistrationClient.deleteRegisteredGuild(leftGuild.getId());
        }).start();

    }
}
