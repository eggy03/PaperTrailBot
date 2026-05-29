package io.github.eggy03.papertrail.bot.listeners.misc;

import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/*
 * This class will have methods that unregister the log channels from the database after the bot has been kicked
 */
@ApplicationScoped
public class SelfKickListener extends ListenerAdapter {

    @NonNull
    private final AuditLogRegistrationClient auditLogRegistrationClient;

    @NonNull
    private final MessageLogRegistrationClient messageLogRegistrationClient;

    @Inject
    public SelfKickListener(@NonNull AuditLogRegistrationClient auditLogRegistrationClient, @NonNull MessageLogRegistrationClient messageLogRegistrationClient) {
        this.auditLogRegistrationClient = auditLogRegistrationClient;
        this.messageLogRegistrationClient = messageLogRegistrationClient;
    }

    @Override
    @Blocking
    @RunOnVirtualThread
    public void onGuildLeave(@NonNull GuildLeaveEvent event) {
        Guild leftGuild = event.getGuild();
        auditLogRegistrationClient.deleteRegisteredGuild(leftGuild.getId());
        messageLogRegistrationClient.deleteRegisteredGuild(leftGuild.getId());
    }
}
