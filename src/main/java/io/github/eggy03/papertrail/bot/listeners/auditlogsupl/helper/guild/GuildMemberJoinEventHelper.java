package io.github.eggy03.papertrail.bot.listeners.auditlogsupl.helper.guild;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.commons.utils.DurationUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

import java.awt.Color;
import java.time.Instant;

@UtilityClass
public class GuildMemberJoinEventHelper {

    public static void format(@NonNull GuildMemberJoinEvent event, @NonNull String channelIdToSendTo) {

        Guild guild = event.getGuild();
        User user = event.getUser();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("🛬 Member Join Event");
        eb.setDescription("A Member has joined " + guild.getName());
        eb.setThumbnail(user.getEffectiveAvatarUrl());
        eb.setColor(Color.GREEN);

        eb.addField("Member Name", "╰┈➤" + user.getName(), false);
        eb.addField("Member Mention", "╰┈➤" + user.getAsMention(), false);
        eb.addField("Member ID", "╰┈➤" + user.getId(), false);
        eb.addField("Account Created", "╰┈➤" + DurationUtils.isoToLocalTimeCounter(user.getTimeCreated()), false);
        eb.addField("Bot Account", "╰┈➤" + BooleanUtils.formatToYesOrNo(user.isBot()), false);

        eb.setFooter(event.getGuild().getName());
        eb.setTimestamp(Instant.now());

        MessageEmbed mb = eb.build();

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
