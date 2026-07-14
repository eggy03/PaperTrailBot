package io.github.eggy03.papertrail.bot.service.handlers.auditlog;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

// for action types not implemented by PaperTrail yet
@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class UnimplementedActionTypeHandler extends GuildAuditLogEntryCreateEventActionTypeHandler {

    @Override
    public void onUnimplementedActionType(@NonNull GuildAuditLogEntryCreateEvent event) {
        log.warn("Unimplemented Action Type: {} with Target Type: {}", event.getEntry().getType(), event.getEntry().getType().getTargetType());
    }
}
