package io.github.eggy03.papertrail.bot.listeners.guild;

import io.github.eggy03.papertrail.bot.annotations.VirtualThreadFactory;
import io.github.eggy03.papertrail.bot.service.handlers.guild.GuildBoostEventHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadFactory;

@Singleton
@Slf4j
public final class GuildBoostEventListener extends ListenerAdapter {

    private final @NonNull GuildBoostEventHandler handler;
    private final @NonNull
    @VirtualThreadFactory ThreadFactory virtualThreadFactory;

    @Inject
    public GuildBoostEventListener(@NonNull GuildBoostEventHandler handler, @NonNull @VirtualThreadFactory ThreadFactory virtualThreadFactory) {
        this.handler = handler;
        this.virtualThreadFactory = virtualThreadFactory;
    }

    @Override
    public void onGuildUpdateBoostTier(@NonNull GuildUpdateBoostTierEvent event) {

        log.debug("Received [Event=GuildUpdateBoostTier] for [Guild={}, ID={}]",
                event.getGuild().getName(), event.getGuild().getId()
        );

        virtualThreadFactory
                .newThread(() -> handler.handleUpdateBoostTier(event))
                .start();
    }

    @Override
    public void onGuildUpdateBoostCount(@NonNull GuildUpdateBoostCountEvent event) {

        log.debug("Received [Event=GuildUpdateBoostCount] for [Guild={}, ID={}]",
                event.getGuild().getName(), event.getGuild().getId()
        );

        virtualThreadFactory
                .newThread(() -> handler.handleUpdateBoostCount(event))
                .start();
    }

    @Override
    public void onGuildMemberUpdateBoostTime(@NonNull GuildMemberUpdateBoostTimeEvent event) {

        log.debug("Received [Event=GuildMemberUpdateBoostTime] for [Guild={}, ID={}]",
                event.getGuild().getName(), event.getGuild().getId()
        );

        virtualThreadFactory
                .newThread(() -> handler.handleMemberUpdateBoostTime(event))
                .start();
    }
}
