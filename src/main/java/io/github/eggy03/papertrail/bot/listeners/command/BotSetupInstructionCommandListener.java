package io.github.eggy03.papertrail.bot.listeners.command;

import io.github.eggy03.papertrail.bot.annotations.VirtualThreadFactory;
import io.github.eggy03.papertrail.bot.handlers.command.BotSetupInstructionCommandHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadFactory;

@Singleton
public final class BotSetupInstructionCommandListener extends ListenerAdapter {

    private final @NonNull BotSetupInstructionCommandHandler handler;
    private final @NonNull
    @VirtualThreadFactory ThreadFactory virtualThreadFactory;

    @Inject
    public BotSetupInstructionCommandListener(@NonNull BotSetupInstructionCommandHandler handler, @NonNull @VirtualThreadFactory ThreadFactory virtualThreadFactory) {
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