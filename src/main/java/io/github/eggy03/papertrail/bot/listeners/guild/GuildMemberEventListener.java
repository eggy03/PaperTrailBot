package io.github.eggy03.papertrail.bot.listeners.guild;

import io.github.eggy03.papertrail.bot.handlers.guild.GuildMemberEventHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

// this event is not properly logged in the audit logs, hence the usage of JDA's listener is preferred
// this event is logged in the same channel where the audit log events are logged
@ApplicationScoped
@Slf4j
public final class GuildMemberEventListener extends ListenerAdapter {

    private final @NonNull GuildMemberEventHandler handler;

    @Inject
    public GuildMemberEventListener(@NonNull GuildMemberEventHandler handler) {
        this.handler = handler;
    }


    @Override
    public void onGuildMemberJoin(@NonNull GuildMemberJoinEvent event) {

        log.info("Received [Event=GuildMemberJoin] for [Guild={}, ID={}]",
                event.getGuild().getName(), event.getGuild().getId()
        );

        Thread.ofVirtual()
                .name("guild-member-join-event-listener-vthread-", 0)
                .start(() -> handler.handleGuildMemberJoin(event));
    }

    @Override
    public void onGuildMemberRemove(@NonNull GuildMemberRemoveEvent event) {

        log.info("Received [Event=GuildMemberRemove] for [Guild={}, ID={}]",
                event.getGuild().getName(), event.getGuild().getId()
        );

        Thread.ofVirtual()
                .name("guild-member-remove-event-listener-vthread-", 0)
                .start(() -> handler.handleGuildMemberRemove(event));
    }

}
