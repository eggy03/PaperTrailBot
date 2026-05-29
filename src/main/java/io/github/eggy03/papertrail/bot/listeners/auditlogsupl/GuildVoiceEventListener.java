package io.github.eggy03.papertrail.bot.listeners.auditlogsupl;

import io.github.eggy03.papertrail.bot.service.auditlogsupl.GuildVoiceEventHelper;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;
import java.util.concurrent.Executor;

// this event is not properly logged in the audit logs, hence the usage of JDA's listener is preferred
// this event is logged in the same channel where the audit log events are logged
@RequiredArgsConstructor
public class GuildVoiceEventListener extends ListenerAdapter {

    @NonNull
    private final AuditLogRegistrationClient client;

    @NonNull
    private final Executor vThreadPool;

    @Override
    public void onGuildVoiceUpdate(@NonNull GuildVoiceUpdateEvent event) {

        vThreadPool.execute(() -> {
            // Call the API and see if the event came from a registered Guild
            Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());
            response.ifPresent(success ->
                    GuildVoiceEventHelper.format(event, success.getChannelId())
            );
        });
    }
}