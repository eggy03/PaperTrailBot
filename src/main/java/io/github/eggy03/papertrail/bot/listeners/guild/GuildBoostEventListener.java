package io.github.eggy03.papertrail.bot.listeners.guild;

import io.github.eggy03.papertrail.bot.handlers.guild.GuildBoostEventHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@ApplicationScoped
public final class GuildBoostEventListener extends ListenerAdapter {

    private final @NonNull GuildBoostEventHandler handler;

    @Inject
    public GuildBoostEventListener(@NonNull GuildBoostEventHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onGuildUpdateBoostTier(@NonNull GuildUpdateBoostTierEvent event) {
        handler.handleUpdateBoostTier(event);
    }

    @Override
    public void onGuildUpdateBoostCount(@NonNull GuildUpdateBoostCountEvent event) {
        handler.handleUpdateBoostCount(event);
    }

    @Override
    public void onGuildMemberUpdateBoostTime(@NonNull GuildMemberUpdateBoostTimeEvent event) {
        handler.handleMemberUpdateBoostTime(event);
    }
}
