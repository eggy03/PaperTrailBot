package io.github.eggy03.papertrail.bot.service.auditlog;

import io.github.eggy03.papertrail.bot.listeners.auditlog.GuildAuditLogEntryCreateEventHandler;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

// for action types not implemented by PaperTrail yet
@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public class UnimplementedEventHandler extends GuildAuditLogEntryCreateEventHandler {

    @Override
    public void onUnimplementedEvent(@NonNull GuildAuditLogEntryCreateEvent event) {
        log.warn("Unimplemented Action Type: {} with Target Type: {}", event.getEntry().getType(), event.getEntry().getType().getTargetType());
    }
}
