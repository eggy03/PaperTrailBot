package io.github.eggy03.papertrail.bot.listeners.message;

import io.github.eggy03.papertrail.bot.annotations.VirtualThreadFactory;
import io.github.eggy03.papertrail.bot.service.handlers.message.GuildMessageEventHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadFactory;

@Singleton
public final class GuildMessageEventListener extends ListenerAdapter {

    private final @NonNull GuildMessageEventHandler handler;
    private final @NonNull
    @VirtualThreadFactory ThreadFactory virtualThreadFactory;

    @Inject
    public GuildMessageEventListener(@NonNull GuildMessageEventHandler handler, @NonNull @VirtualThreadFactory ThreadFactory virtualThreadFactory) {
        this.handler = handler;
        this.virtualThreadFactory = virtualThreadFactory;
    }

    @Override
    public void onMessageReceived(@NonNull MessageReceivedEvent event) {

        // if the author is a bot or system, don't log
        if (event.getAuthor().isBot() || event.getAuthor().isSystem()) {
            return;
        }
        // don't register non-textual contents
        if (event.getMessage().getContentRaw().isEmpty()) {
            return;
        }

        virtualThreadFactory
                .newThread(() -> handler.handleMessageReceivedEvent(event))
                .start();
    }

    @Override
    public void onMessageUpdate(@NonNull MessageUpdateEvent event) {

        if (event.getAuthor().isBot() || event.getAuthor().isSystem()) {
            return;
        }

        virtualThreadFactory
                .newThread(() -> handler.handleMessageUpdateEvent(event))
                .start();
    }

    @Override
    public void onMessageDelete(@NonNull MessageDeleteEvent event) {
        virtualThreadFactory
                .newThread(() -> handler.handleMessageDeleteEvent(event))
                .start();
    }
}
