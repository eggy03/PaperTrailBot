package io.github.eggy03.papertrail.bot.listeners.command;

import io.github.eggy03.papertrail.bot.handlers.command.DebugCommandHandler;
import io.github.eggy03.papertrail.bot.qualifiers.VirtualThreadFactory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadFactory;

@Slf4j
@Singleton
public final class DebugCommandListener extends ListenerAdapter {

    private final @NonNull DebugCommandHandler handler;
    private final @NonNull
    @VirtualThreadFactory ThreadFactory virtualThreadFactory;

    @Inject
    public DebugCommandListener(@NonNull DebugCommandHandler handler, @NonNull @VirtualThreadFactory ThreadFactory virtualThreadFactory) {
        this.handler = handler;
        this.virtualThreadFactory = virtualThreadFactory;
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

        virtualThreadFactory
                .newThread(() -> handler.sendDebugInfo(event, guild, member))
                .start();
    }
}
