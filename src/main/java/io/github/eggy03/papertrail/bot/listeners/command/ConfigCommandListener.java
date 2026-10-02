package io.github.eggy03.papertrail.bot.listeners.command;

import io.github.eggy03.papertrail.bot.annotations.VirtualThreadFactory;
import io.github.eggy03.papertrail.bot.service.handlers.command.ConfigCommandHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadFactory;

@Singleton
public final class ConfigCommandListener extends ListenerAdapter {

    private final @NonNull ConfigCommandHandler handler;
    private final @NonNull ThreadFactory virtualThreadFactory;

    @Inject
    public ConfigCommandListener(@NonNull ConfigCommandHandler handler, @NonNull @VirtualThreadFactory ThreadFactory virtualThreadFactory) {
        this.handler = handler;
        this.virtualThreadFactory = virtualThreadFactory;
    }

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("config")) {
            return;
        }

        virtualThreadFactory.newThread(() -> {
            switch (event.getSubcommandName()) {
                case "save" -> handler.saveGuild(event);
                case "view" -> handler.viewGuild(event);
                case "update" -> handler.updateGuild(event);
                case null, default -> {
                    // do nothing
                }
            }
        }).start();
    }

}
