package io.github.eggy03.papertrail.bot.listeners.command;

import io.github.eggy03.papertrail.bot.annotations.VirtualThreadFactory;
import io.github.eggy03.papertrail.bot.service.handlers.command.MessageLogSetupCommandHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadFactory;

@Slf4j
@Singleton
public final class MessageLogSetupCommandListener extends ListenerAdapter {

    private final @NonNull MessageLogSetupCommandHandler handler;
    private final @NonNull
    @VirtualThreadFactory ThreadFactory virtualThreadFactory;

    @Inject
    public MessageLogSetupCommandListener(@NonNull MessageLogSetupCommandHandler handler, @NonNull @VirtualThreadFactory ThreadFactory virtualThreadFactory) {
        this.handler = handler;
        this.virtualThreadFactory = virtualThreadFactory;
    }

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("messagelog") || event.getSubcommandName() == null) {
            return;
        }

        virtualThreadFactory.newThread(() -> {
            switch (event.getSubcommandName()) {
                case "set" -> handler.setMessageLogging(event);
                case "view" -> handler.viewMessageLoggingChannel(event);
                case "remove" -> handler.unsetMessageLogging(event);
                default -> {
                    // skip
                }
            }
        }).start();
    }
}
