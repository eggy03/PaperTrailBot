package io.github.eggy03.papertrail.bot.listeners.guild;

import io.github.eggy03.papertrail.bot.handlers.guild.GuildPollEventHandler;
import io.github.eggy03.papertrail.bot.qualifiers.VirtualThreadFactory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadFactory;

// guild poll events are mapped to audit log table
@Singleton
@Slf4j
public final class GuildPollEventListener extends ListenerAdapter {

    private final @NonNull GuildPollEventHandler handler;
    private final @NonNull
    @VirtualThreadFactory ThreadFactory virtualThreadFactory;

    @Inject
    public GuildPollEventListener(@NonNull GuildPollEventHandler handler, @NonNull @VirtualThreadFactory ThreadFactory virtualThreadFactory) {
        this.handler = handler;
        this.virtualThreadFactory = virtualThreadFactory;
    }

    @Override
    public void onMessageReceived(@NonNull MessageReceivedEvent event) {

        if (!event.isFromGuild() || event.getMessage().getPoll() == null) {
            return;
        }

        log.debug("Received [Event=GuildPollCreate] for [Guild={}, ID={}]",
                event.getGuild().getName(), event.getGuild().getId()
        );

        virtualThreadFactory
                .newThread(() -> handler.handlePollCreationEvent(event))
                .start();
    }
}
