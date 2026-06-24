package io.github.eggy03.papertrail.bot.listeners.guild;

import io.github.eggy03.papertrail.bot.handlers.guild.GuildSecurityIncidentEventHandler;
import io.github.eggy03.papertrail.bot.qualifiers.VirtualThreadFactory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSecurityIncidentActionsEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSecurityIncidentDetectionsEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadFactory;

@Singleton
@Slf4j
public final class GuildSecurityIncidentEventListener extends ListenerAdapter {

    private final @NonNull GuildSecurityIncidentEventHandler handler;
    private final @NonNull
    @VirtualThreadFactory ThreadFactory virtualThreadFactory;

    @Inject
    public GuildSecurityIncidentEventListener(@NonNull GuildSecurityIncidentEventHandler handler, @NonNull @VirtualThreadFactory ThreadFactory virtualThreadFactory) {
        this.handler = handler;
        this.virtualThreadFactory = virtualThreadFactory;
    }

    @Override
    public void onGuildUpdateSecurityIncidentDetections(@NonNull GuildUpdateSecurityIncidentDetectionsEvent event) {
        log.debug("Received [Event=GuildUpdateSecurityIncidentDetections] for [Guild={}, ID={}]",
                event.getGuild().getName(), event.getGuild().getId()
        );

        virtualThreadFactory
                .newThread(() -> handler.handleGuildUpdateSecurityIncidentDetections(event))
                .start();
    }

    @Override
    public void onGuildUpdateSecurityIncidentActions(@NonNull GuildUpdateSecurityIncidentActionsEvent event) {
        log.debug("Received [Event=GuildUpdateSecurityIncidentActions] for [Guild={}, ID={}]",
                event.getGuild().getName(), event.getGuild().getId()
        );

        virtualThreadFactory
                .newThread(() -> handler.handleGuildUpdateSecurityIncidentActions(event))
                .start();

    }

}
