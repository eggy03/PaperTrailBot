package io.github.eggy03.papertrail.bot.listeners.command;

import io.github.eggy03.papertrail.bot.handlers.command.BotSetupInstructionCommandHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Singleton
public final class BotSetupInstructionCommandListener extends ListenerAdapter {

    private final @NonNull BotSetupInstructionCommandHandler handler;

    @Inject
    public BotSetupInstructionCommandListener(@NonNull BotSetupInstructionCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("setup")) {
            Thread.ofVirtual()
                    .name("bot-setup-instruction-command-listener-vthread-", 0)
                    .start(() -> handler.sendInstructions(event));
        }
    }
}