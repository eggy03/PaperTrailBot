package io.github.eggy03.papertrail.bot.listeners.command;

import io.github.eggy03.papertrail.bot.handlers.command.ServerStatCommandHandler;
import io.github.eggy03.papertrail.bot.qualifiers.VirtualThreadFactory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadFactory;

@Singleton
@Slf4j
public final class ServerStatCommandListener extends ListenerAdapter {

    private final @NonNull ServerStatCommandHandler handler;
    private final @NonNull
    @VirtualThreadFactory ThreadFactory virtualThreadFactory;

    @Inject
    public ServerStatCommandListener(@NonNull ServerStatCommandHandler handler, @NonNull @VirtualThreadFactory ThreadFactory virtualThreadFactory) {
        this.handler = handler;
        this.virtualThreadFactory = virtualThreadFactory;
    }

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("stats")) {
            return;
        }

        Guild guild = event.getGuild();
        if (guild == null) {
            log.warn("Command may have been called outside of a guild");
            return;
        }

        virtualThreadFactory
                .newThread(() -> handler.sendServerStats(event, guild))
                .start();

    }
}
