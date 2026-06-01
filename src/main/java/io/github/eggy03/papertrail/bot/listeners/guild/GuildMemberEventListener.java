package io.github.eggy03.papertrail.bot.listeners.guild;

import io.github.eggy03.papertrail.bot.handlers.guild.GuildMemberEventHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

// this event is not properly logged in the audit logs, hence the usage of JDA's listener is preferred
// this event is logged in the same channel where the audit log events are logged
@ApplicationScoped
public final class GuildMemberEventListener extends ListenerAdapter {

    private final @NonNull GuildMemberEventHandler handler;

    @Inject
    public GuildMemberEventListener(@NonNull GuildMemberEventHandler handler) {
        this.handler = handler;
    }


    @Override
    public void onGuildMemberJoin(@NonNull GuildMemberJoinEvent event) {
        handler.handleGuildMemberJoin(event);
    }

    @Override
    public void onGuildMemberRemove(@NonNull GuildMemberRemoveEvent event) {
        handler.handleGuildMemberRemove(event);
    }

}
