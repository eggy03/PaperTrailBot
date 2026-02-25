package io.github.eggy03.papertrail.bot.main;

import io.github.eggy03.papertrail.bot.listeners.auditlog.event.AuditLogEventListener;
import io.github.eggy03.papertrail.bot.listeners.auditlog.setup.AuditLogSetupCommandListener;
import io.github.eggy03.papertrail.bot.listeners.auditlogsupl.event.guild.GuildBoostEventListener;
import io.github.eggy03.papertrail.bot.listeners.auditlogsupl.event.guild.GuildMemberJoinAndLeaveEventListener;
import io.github.eggy03.papertrail.bot.listeners.auditlogsupl.event.guild.GuildPollEventListener;
import io.github.eggy03.papertrail.bot.listeners.auditlogsupl.event.guild.GuildVoiceEventListener;
import io.github.eggy03.papertrail.bot.listeners.messagelog.event.MessageLogListener;
import io.github.eggy03.papertrail.bot.listeners.messagelog.setup.MessageLogSetupCommandListener;
import io.github.eggy03.papertrail.bot.listeners.misc.BotSetupInstructionCommandListener;
import io.github.eggy03.papertrail.bot.listeners.misc.DebugListener;
import io.github.eggy03.papertrail.bot.listeners.misc.SelfKickListener;
import io.github.eggy03.papertrail.bot.listeners.misc.ServerStatCommandListener;
import io.github.eggy03.papertrail.bot.listeners.misc.SlashCommandRegistrationListener;
import lombok.NonNull;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.concurrent.ExecutorService;

/**
 * A wrapper over {@link DefaultShardManagerBuilder} configured with defaults
 */
public class BootstrapService {

    private final @NonNull DefaultShardManagerBuilder builder;

    public BootstrapService(@NonNull String token) {
        builder = DefaultShardManagerBuilder.createDefault(token);
    }

    public BootstrapService applyRecommendedPreset() {

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
        return this;

    }

    public BootstrapService applyDefaultStatus() {
        builder.setStatus(OnlineStatus.ONLINE);
        return this;
    }

    public BootstrapService applyPreBuildEventListeners(ExecutorService vThreadPool) {

        builder.addEventListeners(

                new AuditLogSetupCommandListener(),
                new AuditLogEventListener(vThreadPool),

                new MessageLogSetupCommandListener(),
                new MessageLogListener(vThreadPool),

                new GuildVoiceEventListener(vThreadPool),
                new GuildMemberJoinAndLeaveEventListener(vThreadPool),
                new GuildPollEventListener(vThreadPool),
                new GuildBoostEventListener(vThreadPool),
                new SelfKickListener(vThreadPool),

                new ServerStatCommandListener(),
                new BotSetupInstructionCommandListener(),
                new DebugListener(vThreadPool),
                new SlashCommandRegistrationListener()
        );
        return this;
    }

    public BootstrapService applySharding(int minShardId, int maxShardId, int totalShards) {
        builder.setShardsTotal(totalShards).setShards(minShardId, maxShardId);
        return this;
    }

    public ShardManager start() {
        return builder.build();
    }

}
