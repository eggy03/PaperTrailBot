package io.github.eggy03.papertrail.bot.listeners.guild;

import io.github.eggy03.papertrail.bot.handlers.guild.GuildBoostEventHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Singleton
@Slf4j
public final class GuildBoostEventListener extends ListenerAdapter {

    private final @NonNull GuildBoostEventHandler handler;

    @Inject
    public GuildBoostEventListener(@NonNull GuildBoostEventHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onGuildUpdateBoostTier(@NonNull GuildUpdateBoostTierEvent event) {

        log.debug("Received [Event=GuildUpdateBoostTier] for [Guild={}, ID={}]",
                event.getGuild().getName(), event.getGuild().getId()
        );

        Thread.ofVirtual()
                .name("guild-update-boost-tier-event-listener-vthread-", 0)
                .start(() -> handler.handleUpdateBoostTier(event));
    }

    @Override
    public void onGuildUpdateBoostCount(@NonNull GuildUpdateBoostCountEvent event) {

        log.debug("Received [Event=GuildUpdateBoostCount] for [Guild={}, ID={}]",
                event.getGuild().getName(), event.getGuild().getId()
        );

        Thread.ofVirtual()
                .name("guild-update-boost-count-event-listener-vthread-", 0)
                .start(() -> handler.handleUpdateBoostCount(event));
    }

    @Override
    public void onGuildMemberUpdateBoostTime(@NonNull GuildMemberUpdateBoostTimeEvent event) {

        log.debug("Received [Event=GuildMemberUpdateBoostTime] for [Guild={}, ID={}]",
                event.getGuild().getName(), event.getGuild().getId()
        );

        Thread.ofVirtual()
                .name("guild-member-update-boost-time-event-listener-vthread-", 0)
                .start(() -> handler.handleMemberUpdateBoostTime(event));
    }
}
