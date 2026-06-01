package io.github.eggy03.papertrail.bot.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import java.util.Objects;
import java.util.stream.Collectors;

@Readiness
@ApplicationScoped
public class ReadinessCheckController implements HealthCheck {

    private final @NonNull ShardManager shardManager;

    @Inject
    public ReadinessCheckController(@NonNull ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public HealthCheckResponse call() {

        if (shardManager.getStatuses().values().stream().allMatch(status -> status == JDA.Status.CONNECTED)) {
            return HealthCheckResponse.named("Readiness Check")
                    .withData("Shard Statuses", "All Shards are connected")
                    .up()
                    .build();
        } else {
            String shardStatuses = shardManager.getStatuses()
                    .values()
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining(", "));

            return HealthCheckResponse.named("Readiness Check")
                    .withData("Shard Statuses", shardStatuses)
                    .down()
                    .build();
        }

    }
}
