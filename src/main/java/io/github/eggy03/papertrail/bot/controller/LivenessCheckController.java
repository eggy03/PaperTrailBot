package io.github.eggy03.papertrail.bot.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Liveness
@ApplicationScoped
public class LivenessCheckController implements HealthCheck {

    private static final Set<JDA.Status> HEALTHY_STATUSES = Set.of(
            JDA.Status.CONNECTED,
            JDA.Status.ATTEMPTING_TO_RECONNECT,
            JDA.Status.RECONNECT_QUEUED,
            JDA.Status.WAITING_TO_RECONNECT
    );

    private final @NonNull ShardManager shardManager;

    @Inject
    public LivenessCheckController(@NonNull ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public HealthCheckResponse call() {

        if (HEALTHY_STATUSES.containsAll(shardManager.getStatuses().values())) {
            return HealthCheckResponse.named("Liveness Check")
                    .withData("Shard Statuses", "All Shards are operating normally")
                    .up()
                    .build();
        } else {
            String shardStatuses = shardManager.getStatuses()
                    .values()
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining(", "));

            return HealthCheckResponse.named("Liveness Check")
                    .withData("Shard Statuses", shardStatuses)
                    .down()
                    .build();
        }

    }
}
