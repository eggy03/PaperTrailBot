package io.github.eggy03.papertrail.bot.listeners.command;

import io.github.eggy03.papertrail.bot.handlers.command.MessageLogSetupCommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Slf4j
@ApplicationScoped
public final class MessageLogSetupCommandListener extends ListenerAdapter {

    private final @NonNull MessageLogSetupCommandHandler handler;

    @Inject
    public MessageLogSetupCommandListener(@NonNull MessageLogSetupCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("messagelog") || event.getSubcommandName() == null) {
            return;
        }

        Thread.ofVirtual().name("message-log-setup-command-listener-vthread-", 0)
                .start(() -> {
                    switch (event.getSubcommandName()) {
                        case "set" -> handler.setMessageLogging(event);
                        case "view" -> handler.viewMessageLoggingChannel(event);
                        case "remove" -> handler.unsetMessageLogging(event);
                        default -> {
                            // skip
                        }
                    }
                });


    }
}
