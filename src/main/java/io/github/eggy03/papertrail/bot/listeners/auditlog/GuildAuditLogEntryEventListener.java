package io.github.eggy03.papertrail.bot.listeners.auditlog;

import io.github.eggy03.papertrail.bot.annotations.VirtualThreadFactory;
import io.github.eggy03.papertrail.bot.service.handlers.auditlog.GuildAuditLogEntryCreateEventActionTypeHandler;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadFactory;

/**
 * Listener responsible for receiving {@link GuildAuditLogEntryCreateEvent}
 * events from JDA and delegating them to all registered
 * {@link GuildAuditLogEntryCreateEventActionTypeHandler} CDI beans.
 *
 * <p>
 * Event handlers are resolved dynamically using
 * {@code Instance<GuildAuditLogEntryCreateEventActionTypeHandler>}, allowing multiple
 * independent handler implementations to process the same audit log event.
 * </p>
 *
 * <p>
 * Each discovered handler instance will receive the event through
 * {@link GuildAuditLogEntryCreateEventActionTypeHandler#handleActionType(GuildAuditLogEntryCreateEvent)}, where
 * it will process the event.
 * </p>
 */
@Slf4j
@Singleton
public final class GuildAuditLogEntryEventListener extends ListenerAdapter {

    private final @NonNull Instance<GuildAuditLogEntryCreateEventActionTypeHandler> handlerInstances;
    private final @NonNull
    @VirtualThreadFactory ThreadFactory virtualThreadFactory;

    @Inject
    public GuildAuditLogEntryEventListener(@NonNull Instance<GuildAuditLogEntryCreateEventActionTypeHandler> handlerInstances,
                                           @NonNull @VirtualThreadFactory ThreadFactory virtualThreadFactory
    ) {
        this.handlerInstances = handlerInstances;
        this.virtualThreadFactory = virtualThreadFactory;
    }

    @Override
    public void onGuildAuditLogEntryCreate(@NonNull GuildAuditLogEntryCreateEvent event) {

        log.debug("Received [Event=GuildAuditLogEntryCreate, ActionType={}] for [Guild={}, ID={}]",
                event.getEntry().getType(), event.getGuild().getName(), event.getGuild().getId()
        );

        /*
        Each handler instance gets its own virtual thread to handle the event's ActionType
        or else, they will be processed sequentially in a single virtual thread.
        This will be particularly slower if two or more inherited classes have overrides
        for handling the same ActionType.
         */
        handlerInstances.forEach(handler ->
                virtualThreadFactory
                        .newThread(() -> handler.handleActionType(event))
                        .start()
        );
    }

}