package io.github.eggy03.papertrail.bot.handlers.guild;

import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;

@ApplicationScoped
@Slf4j
public final class GuildBoostEventHandler {

    private final @NonNull AuditLogRegistrationClient client;

    @Inject
    public GuildBoostEventHandler(@NonNull AuditLogRegistrationClient client) {
        this.client = client;
    }

    @NonNull
    private String getRegisteredChannelId(@NonNull String guildId) {
        return client.getRegisteredGuild(guildId)
                .map(AuditLogRegistrationEntity::getChannelId).orElse(StringUtils.EMPTY);

    }

    private void performChecksThenBuildAndSendEmbed(@NonNull GenericGuildEvent event, @NonNull EmbedBuilder embedBuilder, @NonNull String channelIdToSendTo) {
        if (!embedBuilder.isValidLength() || embedBuilder.isEmpty()) {
            log.warn("Embed is empty or too long (current length: {}).", embedBuilder.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    public void handleUpdateBoostTier(@NonNull GuildUpdateBoostTierEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        Guild guild = event.getGuild();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Server Boost Tier Update");
        eb.setDescription(MarkdownUtil.quoteBlock("Guild Boost Tier Updated\nTarget Guild: " + guild.getName()));
        eb.setThumbnail(guild.getIconUrl());
        eb.setColor(Color.YELLOW);

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

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    public void handleUpdateBoostCount(@NonNull GuildUpdateBoostCountEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        Guild guild = event.getGuild();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Server Boost Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Guild Boost Count Updated\nTarget Guild: " + guild.getName()));
        eb.setThumbnail(guild.getIconUrl());
        eb.setColor(Color.YELLOW);

        eb.addField(MarkdownUtil.underline("Old Boost Count"), "╰┈➤" + event.getOldBoostCount(), false);
        eb.addField(MarkdownUtil.underline("New Boost Count"), "╰┈➤" + event.getNewBoostCount(), false);

        eb.setFooter(guild.getName());
        eb.setTimestamp(Instant.now());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    public void handleMemberUpdateBoostTime(@NonNull GuildMemberUpdateBoostTimeEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        Member member = event.getMember();
        Guild guild = event.getGuild();

        String mentionableMember = member.getAsMention();

        OffsetDateTime newBoostTime = event.getNewTimeBoosted(); // Will be null if the member stopped boosting

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Server Boost Event");
        eb.setThumbnail(guild.getIconUrl());

        if (newBoostTime != null) {
            eb.setDescription(MarkdownUtil.quoteBlock("Booster Gained: " + mentionableMember + "\nTarget Server: " + guild.getName()));
            eb.setColor(Color.PINK);
        } else {
            eb.setDescription(MarkdownUtil.quoteBlock("Booster Lost: " + mentionableMember + "\nTarget Server: " + guild.getName()));
            eb.setColor(Color.GRAY);
        }

        eb.setFooter(guild.getName());
        eb.setTimestamp(Instant.now());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }
}
