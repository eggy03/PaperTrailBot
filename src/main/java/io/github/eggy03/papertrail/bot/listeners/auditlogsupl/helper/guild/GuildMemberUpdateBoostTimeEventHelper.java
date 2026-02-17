package io.github.eggy03.papertrail.bot.listeners.auditlogsupl.helper.guild;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;

import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;

@UtilityClass
@Slf4j
public class GuildMemberUpdateBoostTimeEventHelper {

    public static void format(@NonNull GuildMemberUpdateBoostTimeEvent event, @NonNull String channelIdToSendTo) {
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
