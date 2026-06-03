package io.github.eggy03.papertrail.bot.listeners.guild;

import io.github.eggy03.papertrail.bot.handlers.guild.GuildSecurityIncidentEventHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSecurityIncidentActionsEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSecurityIncidentDetectionsEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@ApplicationScoped
public final class GuildSecurityIncidentEventListener extends ListenerAdapter {

    private final @NonNull GuildSecurityIncidentEventHandler handler;

    @Inject
    public GuildSecurityIncidentEventListener(@NonNull GuildSecurityIncidentEventHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onGuildUpdateSecurityIncidentDetections(@NonNull GuildUpdateSecurityIncidentDetectionsEvent event) {
        handler.handleGuildUpdateSecurityIncidentDetections(event);
    }

    @Override
    public void onGuildUpdateSecurityIncidentActions(@NonNull GuildUpdateSecurityIncidentActionsEvent event) {
        handler.handleGuildUpdateSecurityIncidentActions(event);
    }

}
