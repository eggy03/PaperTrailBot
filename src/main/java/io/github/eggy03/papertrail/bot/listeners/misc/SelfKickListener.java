package io.github.eggy03.papertrail.bot.listeners.misc;

import io.github.eggy03.papertrail.bot.annotations.VirtualThreadFactory;
import io.github.eggy03.papertrail.http.client.PaperTrailGuildClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadFactory;

/*
 * This class will have methods that unregister the log channels from the database after the bot has been kicked
 */
@Singleton
public final class SelfKickListener extends ListenerAdapter {

    @NonNull
    private final PaperTrailGuildClient paperTrailGuildClient;

    private final @NonNull ThreadFactory virtualThreadFactory;

    @Inject
    public SelfKickListener(@NonNull PaperTrailGuildClient paperTrailGuildClient, @NonNull @VirtualThreadFactory ThreadFactory virtualThreadFactory) {
        this.paperTrailGuildClient = paperTrailGuildClient;
        this.virtualThreadFactory = virtualThreadFactory;
    }

    @Override
    public void onGuildLeave(@NonNull GuildLeaveEvent event) {
        Guild leftGuild = event.getGuild();

        virtualThreadFactory.newThread(() -> {
            paperTrailGuildClient.deleteGuild(leftGuild.getId());
        }).start();

    }
}
