package io.github.eggy03.papertrail.bot.listeners.misc;

import io.github.eggy03.papertrail.bot.bean.ApplicationInfo;
import io.quarkus.runtime.ImageMode;
import io.quarkus.runtime.LaunchMode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;

@Singleton
@Slf4j
public final class ActivityUpdateListener extends ListenerAdapter {

    private final @NonNull ShardManager manager;
    private final @NonNull ApplicationInfo applicationInfo;

    @Inject
    public ActivityUpdateListener(@NonNull ShardManager manager, @NonNull ApplicationInfo applicationInfo) {
        this.manager = manager;
        this.applicationInfo = applicationInfo;
    }

    @Override
    public void onReady(@NonNull ReadyEvent event) { // update on cold start
        manager.setActivity(Activity.customStatus(
                "/setup | " + applicationInfo.projectVersion() + " | " + getImageMode() + " | " + getLaunchMode())
        );
    }

    @Override
    public void onGuildJoin(@NonNull GuildJoinEvent event) { // update on guild join
        log.info("Bot Added To [Guild={}, ID={}]", event.getGuild().getName(), event.getGuild().getId());
    }

    @Override
    public void onGuildLeave(@NonNull GuildLeaveEvent event) { // update on guild leave
        log.info("Bot Removed From [Guild={}, ID={}]", event.getGuild().getName(), event.getGuild().getId());
    }

    private @NonNull String getLaunchMode() {
        return switch (LaunchMode.current()) {
            case DEVELOPMENT -> "Dev";
            case NORMAL -> "Production";
            case RUN -> "Production w/ Dev Services";
            case TEST -> "Test";
        };
    }

    private @NonNull String getImageMode() {
        return switch (ImageMode.current()) {
            case JVM -> "JVM";
            case NATIVE_BUILD -> "Native Build Phase";
            case NATIVE_RUN -> "Native";
        };
    }
}