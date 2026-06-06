package io.github.eggy03.papertrail.bot.listeners.guild;

import io.github.eggy03.papertrail.bot.handlers.guild.GuildSecurityIncidentEventHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSecurityIncidentActionsEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSecurityIncidentDetectionsEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@ApplicationScoped
@Slf4j
public final class GuildSecurityIncidentEventListener extends ListenerAdapter {

    private final @NonNull GuildSecurityIncidentEventHandler handler;

    @Inject
    public GuildSecurityIncidentEventListener(@NonNull GuildSecurityIncidentEventHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onGuildUpdateSecurityIncidentDetections(@NonNull GuildUpdateSecurityIncidentDetectionsEvent event) {
        log.info("Received [Event=GuildUpdateSecurityIncidentDetections] for [Guild={}, ID={}]",
                event.getGuild().getName(), event.getGuild().getId()
        );

        Thread.ofVirtual()
                .name("guild-update-security-incident-detection-event-listener-vthread-", 0)
                .start(() -> handler.handleGuildUpdateSecurityIncidentDetections(event));

    }

    @Override
    public void onGuildUpdateSecurityIncidentActions(@NonNull GuildUpdateSecurityIncidentActionsEvent event) {
        log.info("Received [Event=GuildUpdateSecurityIncidentActions] for [Guild={}, ID={}]",
                event.getGuild().getName(), event.getGuild().getId()
        );

        Thread.ofVirtual()
                .name("guild-update-security-incident-action-event-listener-vthread-", 0)
                .start(() -> handler.handleGuildUpdateSecurityIncidentActions(event));

    }

}
