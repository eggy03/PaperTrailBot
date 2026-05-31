package io.github.eggy03.papertrail.bot.listeners.guild;

import io.github.eggy03.papertrail.bot.handlers.guild.GuildMemberJoinEventHelper;
import io.github.eggy03.papertrail.bot.handlers.guild.GuildMemberRemoveEventHelper;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;
import java.util.concurrent.Executor;

// this event is not properly logged in the audit logs, hence the usage of JDA's listener is preferred
// this event is logged in the same channel where the audit log events are logged
@RequiredArgsConstructor
public final class GuildMemberJoinAndLeaveEventListener extends ListenerAdapter {

    @NonNull
    private final AuditLogRegistrationClient client;

    @NonNull
    private final Executor vThreadPool;

    @Override
    public void onGuildMemberJoin(@NonNull GuildMemberJoinEvent event) {

        vThreadPool.execute(() -> {
            // Call the API and see if the event came from a registered Guild
            Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());
            response.ifPresent(success ->
                    GuildMemberJoinEventHelper.format(event, success.getChannelId())
            );
        });
    }

    @Override
    public void onGuildMemberRemove(@NonNull GuildMemberRemoveEvent event) {

        vThreadPool.execute(() -> {
            // Call the API and see if the event came from a registered Guild
            Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());
            response.ifPresent(success ->
                    GuildMemberRemoveEventHelper.format(event, success.getChannelId())
            );
        });
    }

}
