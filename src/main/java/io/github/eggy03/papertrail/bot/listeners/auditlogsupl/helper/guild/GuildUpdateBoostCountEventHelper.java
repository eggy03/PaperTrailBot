package io.github.eggy03.papertrail.bot.listeners.auditlogsupl.helper.guild;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;
import java.time.Instant;

@UtilityClass
@Slf4j
public class GuildUpdateBoostCountEventHelper {

    public static void format(@NonNull GuildUpdateBoostCountEvent event, @NonNull String channelIdToSendTo) {
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
