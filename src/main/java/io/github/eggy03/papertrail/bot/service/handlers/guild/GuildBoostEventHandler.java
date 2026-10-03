package io.github.eggy03.papertrail.bot.service.handlers.guild;

import io.github.eggy03.papertrail.bot.environment.PaperTrailEnvironment;
import io.github.eggy03.papertrail.bot.service.EmbedCheckingService;
import io.github.eggy03.papertrail.http.client.PaperTrailGuildClient;
import io.github.eggy03.papertrail.http.entity.PaperTrailGuild;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.time.OffsetDateTime;

@ApplicationScoped
@Slf4j
public final class GuildBoostEventHandler {

    private final @NonNull PaperTrailGuildClient client;
    private final @NonNull PaperTrailEnvironment environment;
    private final @NonNull EmbedCheckingService embedCheckingService;

    @Inject
    public GuildBoostEventHandler(@NonNull PaperTrailGuildClient client, @NonNull PaperTrailEnvironment environment, @NonNull EmbedCheckingService embedCheckingService) {
        this.client = client;
        this.environment = environment;
        this.embedCheckingService = embedCheckingService;
    }

    @Nullable
    private String getRegisteredChannelId(@NonNull String guildId) {
        return client.getGuild(guildId)
                .map(PaperTrailGuild::guildEventChannelId)
                .orElse(null);

    }

    public void handleUpdateBoostTier(@NonNull GuildUpdateBoostTierEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo == null) return;

        Guild guild = event.getGuild();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Server Boost Tier Update");
        eb.setDescription(MarkdownUtil.quoteBlock("Guild Boost Tier Updated\nTarget Guild: " + guild.getName()));
        eb.setThumbnail(guild.getIconUrl());
        eb.setColor(environment.embedColor().warningColor());

        Guild.BoostTier oldBoostTier = event.getOldBoostTier();
        Guild.BoostTier newBoostTier = event.getNewBoostTier();

        String oldTier = "**Tier:** " + oldBoostTier.name() + "\n" +
                "**Max Emojis:** " + oldBoostTier.getMaxEmojis() + "\n" +
                "**Max File Size:** " + oldBoostTier.getMaxFileSize() + "\n" +
                "**Max Bitrate:** " + oldBoostTier.getMaxBitrate();

        String newTier = "**Tier:** " + newBoostTier.name() + "\n" +
                "**Max Emojis:** " + newBoostTier.getMaxEmojis() + "\n" +
                "**Max File Size:** " + newBoostTier.getMaxFileSize() + "\n" +
                "**Max Bitrate:** " + newBoostTier.getMaxBitrate();

        eb.addField(MarkdownUtil.underline("Old Boost Tier Information"), oldTier, false);
        eb.addField(MarkdownUtil.underline("New Boost Tier Information"), newTier, false);

        eb.setFooter(guild.getName());
        eb.setTimestamp(Instant.now());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }

    public void handleUpdateBoostCount(@NonNull GuildUpdateBoostCountEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo == null) return;

        Guild guild = event.getGuild();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Server Boost Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Guild Boost Count Updated\nTarget Guild: " + guild.getName()));
        eb.setThumbnail(guild.getIconUrl());
        eb.setColor(environment.embedColor().warningColor());

        eb.addField(MarkdownUtil.underline("Old Boost Count"), "╰┈➤" + event.getOldBoostCount(), false);
        eb.addField(MarkdownUtil.underline("New Boost Count"), "╰┈➤" + event.getNewBoostCount(), false);

        eb.setFooter(guild.getName());
        eb.setTimestamp(Instant.now());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }

    public void handleMemberUpdateBoostTime(@NonNull GuildMemberUpdateBoostTimeEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo == null) return;

        Member member = event.getMember();
        Guild guild = event.getGuild();

        String mentionableMember = member.getAsMention();

        OffsetDateTime newBoostTime = event.getNewTimeBoosted(); // Will be null if the member stopped boosting

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Server Boost Event");
        eb.setThumbnail(guild.getIconUrl());

        if (newBoostTime != null) {
            eb.setDescription(MarkdownUtil.quoteBlock("Booster Gained: " + mentionableMember + "\nTarget Server: " + guild.getName()));
            eb.setColor(environment.embedColor().successColor());
        } else {
            eb.setDescription(MarkdownUtil.quoteBlock("Booster Lost: " + mentionableMember + "\nTarget Server: " + guild.getName()));
            eb.setColor(environment.embedColor().destructiveColor());
        }

        eb.setFooter(guild.getName());
        eb.setTimestamp(Instant.now());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }
}
