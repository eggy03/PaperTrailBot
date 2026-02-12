package io.github.eggy03.papertrail.bot.listeners.supplementaryaudit.event.guild;

import io.github.eggy03.papertrail.bot.commons.utils.EnvConfig;
import io.github.eggy03.papertrail.bot.listeners.supplementaryaudit.helper.guild.GuildPollEventHelper;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;
import java.util.concurrent.Executor;

// guild poll events are mapped to audit log table
@RequiredArgsConstructor
public class GuildPollEventListener extends ListenerAdapter {

    @NonNull
    private static final AuditLogRegistrationClient client = new AuditLogRegistrationClient(EnvConfig.get("API_URL"));

    @NonNull
    private final Executor vThreadPool;

    @Override
    public void onMessageReceived(@NonNull MessageReceivedEvent event) {

        if (!event.isFromGuild())
            return;

        // check if message has a poll
        MessagePoll messagePoll = event.getMessage().getPoll();
        if (messagePoll == null)
            return;

        vThreadPool.execute(() -> {
            // Call the API and see if the event came from a registered Guild
            Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());
            response.ifPresent(success ->
                    GuildPollEventHelper.format(event, messagePoll, success.getChannelId())
            );
        });
    }
}
