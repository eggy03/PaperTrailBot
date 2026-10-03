package io.github.eggy03.papertrail.bot.listeners.command;

import io.github.eggy03.papertrail.bot.annotations.VirtualThreadFactory;
import io.github.eggy03.papertrail.bot.service.handlers.command.HelpCommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadFactory;

@ApplicationScoped
public final class HelpCommandListener extends ListenerAdapter {

    private final @NonNull HelpCommandHandler handler;
    private final @NonNull ThreadFactory virtualThreadFactory;

    @Inject
    public HelpCommandListener(@NonNull HelpCommandHandler handler, @NonNull @VirtualThreadFactory ThreadFactory virtualThreadFactory) {
        this.handler = handler;
        this.virtualThreadFactory = virtualThreadFactory;
    }

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("setup")) {

            virtualThreadFactory
                    .newThread(() -> handler.sendInstructions(event))
                    .start();

        }
    }
}