package io.github.eggy03.papertrail.bot.listeners.guild;

import io.github.eggy03.papertrail.bot.annotations.VirtualThreadFactory;
import io.github.eggy03.papertrail.bot.handlers.guild.GuildMemberEventHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadFactory;

// this event is not properly logged in the audit logs, hence the usage of JDA's listener is preferred
// this event is logged in the same channel where the audit log events are logged
@Singleton
@Slf4j
public final class GuildMemberEventListener extends ListenerAdapter {

    private final @NonNull GuildMemberEventHandler handler;
    private final @NonNull
    @VirtualThreadFactory ThreadFactory virtualThreadFactory;

    @Inject
    public GuildMemberEventListener(@NonNull GuildMemberEventHandler handler, @NonNull @VirtualThreadFactory ThreadFactory virtualThreadFactory) {
        this.handler = handler;
        this.virtualThreadFactory = virtualThreadFactory;
    }


    @Override
    public void onGuildMemberJoin(@NonNull GuildMemberJoinEvent event) {

        log.debug("Received [Event=GuildMemberJoin] for [Guild={}, ID={}]",
                event.getGuild().getName(), event.getGuild().getId()
        );

        virtualThreadFactory
                .newThread(() -> handler.handleGuildMemberJoin(event))
                .start();
    }

    @Override
    public void onGuildMemberRemove(@NonNull GuildMemberRemoveEvent event) {

        log.debug("Received [Event=GuildMemberRemove] for [Guild={}, ID={}]",
                event.getGuild().getName(), event.getGuild().getId()
        );

        virtualThreadFactory
                .newThread(() -> handler.handleGuildMemberRemove(event))
                .start();
    }

}
