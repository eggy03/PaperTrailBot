package io.github.eggy03.papertrail.bot.listeners.command;

import io.github.eggy03.papertrail.bot.handlers.command.BotSetupInstructionCommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@ApplicationScoped
public final class BotSetupInstructionCommandListener extends ListenerAdapter {

    private final @NonNull BotSetupInstructionCommandHandler handler;

    @Inject
    public BotSetupInstructionCommandListener(@NonNull BotSetupInstructionCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("setup")) {
            handler.sendInstructions(event);
        }
    }
}