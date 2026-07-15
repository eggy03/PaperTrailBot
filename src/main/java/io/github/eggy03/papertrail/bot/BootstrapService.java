package io.github.eggy03.papertrail.bot;

import io.github.eggy03.papertrail.bot.configuration.PaperTrailConfig;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestConfig;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Slf4j
@ApplicationScoped
@Startup
public final class BootstrapService {

    private final @NonNull PaperTrailConfig paperTrailConfig;
    private final @NonNull Instance<ListenerAdapter> listeners;
    private final @NonNull ShardManager shardManager;

    @Inject
    public BootstrapService(@NonNull PaperTrailConfig paperTrailConfig, @NonNull Instance<ListenerAdapter> listeners) {
        this.paperTrailConfig = paperTrailConfig;
        this.listeners = listeners;
        this.shardManager = constructShardManager();
    }

    @NonNull
    ShardManager constructShardManager() {

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(paperTrailConfig.discord().token());

        builder.enableIntents(GatewayIntent.SCHEDULED_EVENTS,
                GatewayIntent.AUTO_MODERATION_EXECUTION,
                GatewayIntent.AUTO_MODERATION_CONFIGURATION,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_EXPRESSIONS,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MODERATION
        );

        //cache all members
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        // cache all activities
        builder.enableCache(CacheFlag.ACTIVITY,
                CacheFlag.CLIENT_STATUS,
                CacheFlag.EMOJI,
                CacheFlag.STICKER,
                CacheFlag.ONLINE_STATUS,
                CacheFlag.MEMBER_OVERRIDES,
                CacheFlag.ROLE_TAGS,
                CacheFlag.SCHEDULED_EVENTS
        );
        // chunk all guilds
        builder.setChunkingFilter(ChunkingFilter.ALL);

        // set status
        builder.setStatus(OnlineStatus.ONLINE);

        // add listeners
        listeners.forEach(listener -> {
            log.debug("Registering Listener: {}", listener.getClass().getSimpleName());
            builder.addEventListeners(listener);
        });

        // add shards
        builder.setShardsTotal(paperTrailConfig.discord().shard().total());
        builder.setShards(paperTrailConfig.discord().shard().min(), paperTrailConfig.discord().shard().max());

        // add custom twilight http proxy url if present
        // note the current implementation only changes the proxy
        // JDA's internal rate limit logic still applies on top of the proxy
        // you need to provide a custom RestRateLimiter config
        if (!paperTrailConfig.discord().twilightProxyUrl().isBlank()) { // don't use isEmpty cause default value is a whitespace
            builder.setRestConfig(new RestConfig().setBaseUrl(paperTrailConfig.discord().twilightProxyUrl()));
        }

        // build shard manager and login
        return builder.build();
    }

    @Produces
    @ApplicationScoped
    @NonNull
    ShardManager getShardManager() {
        return shardManager;
    }

    @PreDestroy
    void shutdown() {
        for (int i = paperTrailConfig.discord().shard().min(); i <= paperTrailConfig.discord().shard().max(); i++) {
            log.info("Shutting Down Shard: {}", i);
            shardManager.shutdown(i);
        }
    }
}
