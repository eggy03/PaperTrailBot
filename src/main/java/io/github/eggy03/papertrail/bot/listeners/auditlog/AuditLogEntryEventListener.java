package io.github.eggy03.papertrail.bot.listeners.auditlog;

import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;

/**
 * Listens for {@link GuildAuditLogEntryCreateEvent} events that happen in registered guilds
 * and passes the responsibility of handling them on to {@link AbstractAuditLogEntryEventHandler}
 *
 */
@Slf4j
@ApplicationScoped
public final class AuditLogEntryEventListener extends ListenerAdapter {

    private final @NonNull AuditLogRegistrationClient client;
    private final @NonNull Instance<AbstractAuditLogEntryEventHandler> eventHandlerInstance;

    @Inject
    public AuditLogEntryEventListener(@NonNull AuditLogRegistrationClient client, @NonNull Instance<AbstractAuditLogEntryEventHandler> eventHandlerInstance) {
        this.client = client;
        this.eventHandlerInstance = eventHandlerInstance;
    }

    @Override
    @RunOnVirtualThread
    public void onGuildAuditLogEntryCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
        // Call the API and see if the event came from a registered Guild
        Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());

        response.ifPresent(success ->
                eventHandlerInstance.forEach(handler ->
                        handler.handleEvent(event, success.getChannelId())
                )
        );
    }

}