package io.github.eggy03.papertrail.bot.listeners.auditlog;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Listener responsible for receiving {@link GuildAuditLogEntryCreateEvent}
 * events from JDA and delegating them to all registered
 * {@link GuildAuditLogEntryCreateEventHandler} CDI beans.
 *
 * <p>
 * Event handlers are resolved dynamically using
 * {@code Instance<GuildAuditLogEntryCreateEventHandler>}, allowing multiple
 * independent handler implementations to process the same audit log event.
 * </p>
 *
 * <p>
 * Each discovered handler instance will receive the event through
 * {@link GuildAuditLogEntryCreateEventHandler#handleEvent(GuildAuditLogEntryCreateEvent)}.
 * This enables a multicast-style event processing pipeline where multiple
 * handler beans may react to the same audit log action independently.
 * </p>
 */
@Slf4j
@ApplicationScoped
public final class GuildAuditLogEntryEventListener extends ListenerAdapter {

    private final @NonNull Instance<GuildAuditLogEntryCreateEventHandler> guildAuditLogEntryCreateEventHandlers;

    @Inject
    public GuildAuditLogEntryEventListener(@NonNull Instance<GuildAuditLogEntryCreateEventHandler> guildAuditLogEntryCreateEventHandlers) {
        this.guildAuditLogEntryCreateEventHandlers = guildAuditLogEntryCreateEventHandlers;
    }

    @Override
    public void onGuildAuditLogEntryCreate(@NonNull GuildAuditLogEntryCreateEvent event) {

        Thread.ofVirtual()
                .name("guild-audit-log-entry-create-event-listener-vthread-", 0)
                .start(() -> guildAuditLogEntryCreateEventHandlers.forEach(handler -> handler.handleEvent(event))
                );
    }

}