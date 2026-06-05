package io.github.eggy03.papertrail.bot.listeners.guild;

import io.github.eggy03.papertrail.bot.handlers.guild.GuildVoiceEventHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

// this event is not properly logged in the audit logs, hence the usage of JDA's listener is preferred
// this event is logged in the same channel where the audit log events are logged
@ApplicationScoped
public final class GuildVoiceEventListener extends ListenerAdapter {

    private final @NonNull GuildVoiceEventHandler handler;

    @Inject
    public GuildVoiceEventListener(@NonNull GuildVoiceEventHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onGuildVoiceUpdate(@NonNull GuildVoiceUpdateEvent event) {
        Thread.ofVirtual()
                .name("guild-voice-update-event-listener-vthread-", 0)
                .start(() -> handler.handleVoiceUpdateEvent(event));

    }
}