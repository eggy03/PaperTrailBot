package io.github.eggy03.papertrail.bot.listeners.misc;

import io.github.eggy03.papertrail.bot.commons.constant.ProjectInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;

/*
 * This class updates the number of servers the bot is in
 */
@RequiredArgsConstructor
public class ActivityUpdateListener extends ListenerAdapter {

    @NonNull
    private final ShardManager manager;

    @Override
    public void onReady(@NotNull ReadyEvent event) { // update on cold start
        updateActivity();
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) { // update on guild join
        updateActivity();
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) { // update on guild leave
        updateActivity();
    }

    private void updateActivity() {
        manager.setActivity(Activity.customStatus("/setup | " + manager.getGuildCache().size() + " Servers | " + ProjectInfo.VERSION));
    }

}