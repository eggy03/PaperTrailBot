package io.github.eggy03.papertrail.bot.listeners.command;

import io.github.eggy03.papertrail.bot.handlers.command.AuditLogSetupCommandHandler;
import io.github.eggy03.papertrail.bot.qualifiers.VirtualThreadFactory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadFactory;

@Singleton
public final class AuditLogSetupCommandListener extends ListenerAdapter {

    private final @NonNull AuditLogSetupCommandHandler handler;
    private final @NonNull
    @VirtualThreadFactory ThreadFactory virtualThreadFactory;

    @Inject
    public AuditLogSetupCommandListener(@NonNull AuditLogSetupCommandHandler handler, @NonNull @VirtualThreadFactory ThreadFactory virtualThreadFactory) {
        this.handler = handler;
        this.virtualThreadFactory = virtualThreadFactory;
    }

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("auditlog") || event.getSubcommandName() == null) {
            return;
        }

        virtualThreadFactory.newThread(() -> {
            switch (event.getSubcommandName()) {
                case "set" -> handler.setAuditLogging(event);
                case "view" -> handler.viewAuditLoggingChannel(event);
                case "remove" -> handler.unsetAuditLogging(event);
                default -> {
                    // do nothing
                }
            }
        }).start();
    }

}
