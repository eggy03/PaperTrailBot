package io.github.eggy03.papertrail.bot.listeners.misc;

import io.github.eggy03.papertrail.bot.constant.ProjectInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;

@ApplicationScoped
public final class ActivityUpdateListener extends ListenerAdapter {

    @NonNull
    private final ShardManager manager;

    @Inject
    public ActivityUpdateListener(@NonNull ShardManager manager) {
        this.manager = manager;
    }

    @Override
    public void onReady(@NonNull ReadyEvent event) { // update on cold start
        updateActivity();
    }

    @Override
    public void onGuildJoin(@NonNull GuildJoinEvent event) { // update on auditlogsupl join
        updateActivity();
    }

    @Override
    public void onGuildLeave(@NonNull GuildLeaveEvent event) { // update on auditlogsupl leave
        updateActivity();
    }

    private void updateActivity() {
        manager.setActivity(Activity.customStatus("/setup | " + manager.getGuildCache().size() + " Servers | " + ProjectInfo.VERSION));
    }

}