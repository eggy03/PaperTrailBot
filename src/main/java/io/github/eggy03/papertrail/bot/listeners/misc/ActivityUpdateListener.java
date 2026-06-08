package io.github.eggy03.papertrail.bot.listeners.misc;

import io.github.eggy03.papertrail.bot.constant.ProjectInfo;
import io.quarkus.runtime.ImageMode;
import io.quarkus.runtime.LaunchMode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;

@ApplicationScoped
@Slf4j
public final class ActivityUpdateListener extends ListenerAdapter {

    private final @NonNull ShardManager manager;

    @Inject
    public ActivityUpdateListener(@NonNull ShardManager manager) {
        this.manager = manager;
    }

    @Override
    public void onReady(@NonNull ReadyEvent event) { // update on cold start
        manager.setActivity(Activity.customStatus(
                "/setup | " + ProjectInfo.VERSION + " | " + getImageMode() + " | " + getLaunchMode())
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