package io.github.eggy03.papertrail.bot.listeners.audit.event.guild;

import io.github.eggy03.papertrail.bot.commons.utils.EnvConfig;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.Executor;

// Experimental
@RequiredArgsConstructor
public class GuildBoostEventListener extends ListenerAdapter {

    @NonNull
    private static final AuditLogRegistrationClient client = new AuditLogRegistrationClient(EnvConfig.get("API_URL"));
    private final Executor vThreadPool;

    @Override
    public void onGuildUpdateBoostTier(@NonNull GuildUpdateBoostTierEvent event) {

        vThreadPool.execute(() -> {
            Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());
            response.ifPresent(auditLogObject -> {

                String registeredChannelId = auditLogObject.getChannelId();

                Guild guild = event.getGuild();

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Server Boost Tier Update");
                eb.setDescription(guild.getName() + " has had it's boost tier updated");

                eb.setThumbnail(guild.getIconUrl());
                eb.setImage(guild.getBannerUrl());
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

                eb.addField("Old Boost Tier Information", oldTier, false);
                eb.addField("New Boost Tier Information", newTier, false);

                eb.setFooter(guild.getName());
                eb.setTimestamp(Instant.now());

                MessageEmbed mb = eb.build();

                TextChannel sendingChannel = event.getGuild().getTextChannelById(registeredChannelId);
                if (sendingChannel != null && sendingChannel.canTalk()) {
                    sendingChannel.sendMessageEmbeds(mb).queue();
                }
            });
        });
    }

    @Override
    public void onGuildUpdateBoostCount(@NonNull GuildUpdateBoostCountEvent event) {

        vThreadPool.execute(() -> {
            Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());
            response.ifPresent(success -> {
                String registeredChannelId = success.getChannelId();

                Guild guild = event.getGuild();

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Server Boost Event");
                eb.setDescription("**" + guild.getName() + "** has had it's boost count updated!");

                eb.setThumbnail(guild.getIconUrl());
                eb.setImage(guild.getBannerUrl());

                eb.setColor(Color.YELLOW);

                eb.addField("Old Boost Count", "╰┈➤" + event.getOldBoostCount(), false);
                eb.addField("New Boost Count", "╰┈➤" + event.getNewBoostCount(), false);

                eb.setFooter(guild.getName());
                eb.setTimestamp(Instant.now());

                MessageEmbed mb = eb.build();

                TextChannel sendingChannel = event.getGuild().getTextChannelById(registeredChannelId);
                if (sendingChannel != null && sendingChannel.canTalk()) {
                    sendingChannel.sendMessageEmbeds(mb).queue();
                }
            });
        });
    }

    @Override
    public void onGuildMemberUpdateBoostTime(@NonNull GuildMemberUpdateBoostTimeEvent event) {

        vThreadPool.execute(() -> {
            Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());
            response.ifPresent(success -> {
                String registeredChannelId = success.getChannelId();

                Member member = event.getMember();
                Guild guild = event.getGuild();

                String mentionableMember = member.getAsMention();

                OffsetDateTime newBoostTime = event.getNewTimeBoosted(); // Will be null if the member stopped boosting

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Server Boost Event");
                eb.setThumbnail(guild.getIconUrl());
                eb.setImage(guild.getBannerUrl());

                if (newBoostTime != null) {
                    eb.setDescription("**" + guild.getName() + "** has been boosted!");
                    eb.setColor(Color.PINK);
                    eb.addField("Booster Gained", "╰┈➤" + mentionableMember + " has applied their first boost to your server", false);
                } else {
                    eb.setDescription("**" + guild.getName() + "** has lost a boost.");
                    eb.setColor(Color.GRAY);
                    eb.addField("Booster Lost", "╰┈➤" + mentionableMember + " has removed their boost from your server", false);
                }

                eb.setFooter(guild.getName());
                eb.setTimestamp(Instant.now());

                MessageEmbed mb = eb.build();

                TextChannel sendingChannel = event.getGuild().getTextChannelById(registeredChannelId);
                if (sendingChannel != null && sendingChannel.canTalk()) {
                    sendingChannel.sendMessageEmbeds(mb).queue();
                }
            });
        });
    }
}
