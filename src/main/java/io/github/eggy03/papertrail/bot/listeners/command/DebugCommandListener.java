package io.github.eggy03.papertrail.bot.listeners.command;

import io.github.eggy03.papertrail.bot.handlers.command.DebugCommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Slf4j
@ApplicationScoped
public final class DebugCommandListener extends ListenerAdapter {

    private final @NonNull DebugCommandHandler handler;

    @Inject
    public DebugCommandListener(@NonNull DebugCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("debug"))
            return;

        Guild guild = event.getGuild();
        Member member = event.getMember();
        if (guild == null || member == null) {
            log.warn("command may have been called outside of a guild");
            return;
        }

        handler.sendDebugInfo(event, guild, member);

    }
}
