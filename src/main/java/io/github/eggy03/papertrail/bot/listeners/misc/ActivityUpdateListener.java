package io.github.eggy03.papertrail.bot.listeners.misc;

import io.github.eggy03.papertrail.bot.environment.PaperTrailEnvironment;
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
    private final @NonNull PaperTrailEnvironment paperTrailEnvironment;

    @Inject
    public ActivityUpdateListener(@NonNull ShardManager manager, @NonNull PaperTrailEnvironment paperTrailEnvironment) {
        this.manager = manager;
        this.paperTrailEnvironment = paperTrailEnvironment;
    }

    @Override
    public void onReady(@NonNull ReadyEvent event) { // update on cold start

        String customActivity = paperTrailEnvironment.general().appActivity();
        if (customActivity.isBlank()) {
            manager.setActivity(Activity.customStatus("/help | v" + paperTrailEnvironment.general().appVersion()));
        } else
            manager.setActivity(Activity.customStatus(customActivity));

    }

    @Override
    public void onGuildJoin(@NonNull GuildJoinEvent event) { // update on guild join
        log.info("Bot Added To [Guild={}, ID={}]", event.getGuild().getName(), event.getGuild().getId());
    }

    @Override
    public void onGuildLeave(@NonNull GuildLeaveEvent event) { // update on guild leave
        log.info("Bot Removed From [Guild={}, ID={}]", event.getGuild().getName(), event.getGuild().getId());
    }
}