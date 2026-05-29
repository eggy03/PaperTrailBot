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
 * Listener responsible for receiving {@link GuildAuditLogEntryCreateEvent}
 * events from JDA and delegating them to all registered
 * {@link AuditLogEntryEventHandler} CDI beans.
 *
 * <p>
 * Before dispatching the event, the listener verifies that the guild
 * is registered through {@link AuditLogRegistrationClient}.
 * </p>
 *
 * <p>
 * Event handlers are resolved dynamically using
 * {@code Instance<AuditLogEntryEventHandler>}, allowing multiple
 * independent handler implementations to process the same audit log event.
 * </p>
 *
 * <p>
 * Each discovered handler instance will receive the event through
 * {@link AuditLogEntryEventHandler#handleEvent(GuildAuditLogEntryCreateEvent, String)}.
 * This enables a multicast-style event processing pipeline where multiple
 * handler beans may react to the same audit log action independently.
 * </p>
 *
 * <p>
 * Event processing is executed on a virtual thread via
 * {@link RunOnVirtualThread}.
 * </p>
 */
@Slf4j
@ApplicationScoped
public final class AuditLogEntryEventListener extends ListenerAdapter {

    private final @NonNull AuditLogRegistrationClient client;
    private final @NonNull Instance<AuditLogEntryEventHandler> eventHandlerInstance;

    @Inject
    public AuditLogEntryEventListener(@NonNull AuditLogRegistrationClient client, @NonNull Instance<AuditLogEntryEventHandler> eventHandlerInstance) {
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