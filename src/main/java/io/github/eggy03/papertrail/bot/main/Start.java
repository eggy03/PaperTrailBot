package io.github.eggy03.papertrail.bot.main;

import io.github.eggy03.papertrail.bot.commons.utils.EnvConfig;
import io.github.eggy03.papertrail.bot.listeners.misc.ActivityUpdateListener;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * The main class of the bot
 */
public class Start {

    public static void main(String[] args) {

        // get env vars
        String token = EnvConfig.get("TOKEN");
        String minShardId = EnvConfig.get("MIN_SHARD_ID"); // min for this instance
        String maxShardId = EnvConfig.get("MAX_SHARD_ID"); // max for this instance
        String totalShards = EnvConfig.get("TOTAL_SHARDS"); // total shards for all instances

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
    }

}
