package io.github.eggy03.papertrail.bot.listeners.command;

import io.github.eggy03.papertrail.bot.handlers.command.AuditLogSetupCommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@ApplicationScoped
@Slf4j
public final class AuditLogSetupCommandListener extends ListenerAdapter {

    private final @NonNull AuditLogSetupCommandHandler handler;

    @Inject
    public AuditLogSetupCommandListener(@NonNull AuditLogSetupCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("auditlog") || event.getSubcommandName() == null) {
            return;
        }

        Thread.ofVirtual()
                .name("audit-log-setup-command-listener-vthread-", 0)
                .start(() -> {
                    switch (event.getSubcommandName()) {
                        case "set" -> handler.setAuditLogging(event);
                        case "view" -> handler.viewAuditLoggingChannel(event);
                        case "remove" -> handler.unsetAuditLogging(event);
                        default -> {
                            // do nothing
                        }
                    }
                });

    }

}
