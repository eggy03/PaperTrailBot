package io.github.eggy03.papertrail.bot.listeners.auditlogsupl.helper.guild;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;
import java.time.Instant;

@UtilityClass
@Slf4j
public class GuildUpdateBoostTierEventHelper {

    public static void format(@NonNull GuildUpdateBoostTierEvent event, @NonNull String channelIdToSendTo) {

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

        if (!eb.isValidLength() || eb.isEmpty()) {
            log.warn("Embed is empty or too long (current length: {}).", eb.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(eb.build()).queue();
        }
    }
}
