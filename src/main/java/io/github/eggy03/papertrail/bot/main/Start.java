package io.github.eggy03.papertrail.bot.main;

import io.github.eggy03.papertrail.bot.listeners.misc.ActivityUpdateListener;
import io.github.eggy03.papertrail.bot.utils.EnvConfig;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogContentClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * The main class of the bot
 */
public class Start {

    static void main() {

        // get env vars
        final String token = Objects.requireNonNull(EnvConfig.get("TOKEN"), "Discord Token cannot be null");
        final String papertrailApiBaseUrl = Objects.requireNonNull(EnvConfig.get("API_URL"), "PaperTrail API URL cannot be null");
        final String minShardId = EnvConfig.get("MIN_SHARD_ID"); // min for this instance
        final String maxShardId = EnvConfig.get("MAX_SHARD_ID"); // max for this instance
        final String totalShards = EnvConfig.get("TOTAL_SHARDS"); // total shards for all instances
        final String customBaseUrlProxy = EnvConfig.get("PROXY_URL"); // custom proxy url for the rate limited twilight discord http proxy

        // set v-thread pools for listeners
        final ExecutorService vThreadPool = Executors.newVirtualThreadPerTaskExecutor();

        // initialize SDK clients to call papertrail API service
        final AuditLogRegistrationClient auditLogRegistrationClient = new AuditLogRegistrationClient(papertrailApiBaseUrl);
        final MessageLogRegistrationClient messageLogRegistrationClient = new MessageLogRegistrationClient(papertrailApiBaseUrl);
        final MessageLogContentClient messageLogContentClient = new MessageLogContentClient(papertrailApiBaseUrl);

        // boostrap the bot
        ShardManager manager = new BootstrapService(token)
                .applyRecommendedPreset()
                .applyDefaultStatus()
                .applyPreBuildEventListeners(vThreadPool, auditLogRegistrationClient, messageLogRegistrationClient, messageLogContentClient)
                .applySharding(minShardId, maxShardId, totalShards)
                .applyProxyConfig(customBaseUrlProxy)
                .start();

        // post build listeners that require a fully built shard manager
        manager.addEventListener(new ActivityUpdateListener(manager));
    }

}
