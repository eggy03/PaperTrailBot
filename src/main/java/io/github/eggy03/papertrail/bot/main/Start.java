package io.github.eggy03.papertrail.bot.main;

import io.github.eggy03.papertrail.bot.commons.utils.EnvConfig;
import io.github.eggy03.papertrail.bot.listeners.misc.ActivityUpdateListener;
import io.javalin.Javalin;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * The main class of the bot
 */
public class Start {

    private static Javalin setHealthCheckEndpoint(int runningShards, int totalShards, int port) {
        return Javalin
                .create(config -> {
                    config.concurrency.useVirtualThreads = true;
                    config.routes.get("/health", ctx -> {
                        if (runningShards != totalShards)
                            ctx.status(503).result("Not Ready");
                        else ctx.status(200).result("OK");
                    });
                })
                .start(port);
    }

    public static void main(String[] args) {

        // get env vars
        String token = EnvConfig.get("TOKEN");
        int port = Integer.parseInt(EnvConfig.get("PORT"));
        int minShardId = Integer.parseInt(EnvConfig.get("MIN_SHARD_ID")); // min for this instance
        int maxShardId = Integer.parseInt(EnvConfig.get("MAX_SHARD_ID")); // max for this instance
        int totalShards = Integer.parseInt(EnvConfig.get("TOTAL_SHARDS"));

        // set v-thread pools for listeners
        ExecutorService vThreadPool = Executors.newVirtualThreadPerTaskExecutor();

        // boostrap the bot
        ShardManager manager = new BootstrapService(token)
                .applyRecommendedPreset()
                .applyDefaultStatus()
                .applyPreBuildEventListeners(vThreadPool)
                .applySharding(minShardId, maxShardId, totalShards)
                .start();
        // post build listeners that require a fully built shard manager
        manager.addEventListener(new ActivityUpdateListener(manager));

        // set a health check endpoint for containers
        Javalin endpoint = setHealthCheckEndpoint(manager.getShardsRunning(), manager.getShardsTotal(), port);

        // add shutdown hooks
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            endpoint.stop();
            vThreadPool.shutdown();
        }));
    }

}
