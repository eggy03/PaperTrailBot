package io.github.eggy03.papertrail.bot.listeners.message;

import io.github.eggy03.papertrail.bot.handlers.message.GuildMessageEventHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@ApplicationScoped
public final class GuildMessageEventListener extends ListenerAdapter {

    private final @NonNull GuildMessageEventHandler handler;

    @Inject
    public GuildMessageEventListener(@NonNull GuildMessageEventHandler handler) {
        this.handler = handler;
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

        handler.handleMessageReceivedEvent(event);

    }

    @Override
    public void onMessageUpdate(@NonNull MessageUpdateEvent event) {

        if (event.getAuthor().isBot() || event.getAuthor().isSystem()) {
            return;
        }

        handler.handleMessageUpdateEvent(event);

    }

    @Override
    public void onMessageDelete(@NonNull MessageDeleteEvent event) {
        handler.handleMessageDeleteEvent(event);
    }
}
