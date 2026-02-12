package io.github.eggy03.papertrail.bot.listeners.supplementaryaudit.event.guild;

import io.github.eggy03.papertrail.bot.commons.utils.EnvConfig;
import io.github.eggy03.papertrail.bot.listeners.supplementaryaudit.helper.guild.GuildMemberUpdateBoostTimeEventHelper;
import io.github.eggy03.papertrail.bot.listeners.supplementaryaudit.helper.guild.GuildUpdateBoostCountEventHelper;
import io.github.eggy03.papertrail.bot.listeners.supplementaryaudit.helper.guild.GuildUpdateBoostTierEventHelper;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;
import java.util.concurrent.Executor;

// Experimental
@RequiredArgsConstructor
public class GuildBoostEventListener extends ListenerAdapter {

    @NonNull
    private static final AuditLogRegistrationClient client = new AuditLogRegistrationClient(EnvConfig.get("API_URL"));

    @NonNull
    private final Executor vThreadPool;

    @Override
    public void onGuildUpdateBoostTier(@NonNull GuildUpdateBoostTierEvent event) {

        vThreadPool.execute(() -> {
            Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());
            response.ifPresent(success ->
                    GuildUpdateBoostTierEventHelper.format(event, success.getChannelId())
            );
        });
    }

    @Override
    public void onGuildUpdateBoostCount(@NonNull GuildUpdateBoostCountEvent event) {

        vThreadPool.execute(() -> {
            Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());
            response.ifPresent(success ->
                    GuildUpdateBoostCountEventHelper.format(event, success.getChannelId())
            );
        });
    }

    @Override
    public void onGuildMemberUpdateBoostTime(@NonNull GuildMemberUpdateBoostTimeEvent event) {

        vThreadPool.execute(() -> {
            Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());
            response.ifPresent(success ->
                    GuildMemberUpdateBoostTimeEventHelper.format(event, success.getChannelId())
            );
        });
    }
}
