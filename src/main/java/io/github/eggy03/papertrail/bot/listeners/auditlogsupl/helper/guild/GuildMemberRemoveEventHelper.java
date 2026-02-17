package io.github.eggy03.papertrail.bot.listeners.auditlogsupl.helper.guild;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.time.Instant;

@UtilityClass
@Slf4j
public class GuildMemberRemoveEventHelper {

    public static void format(@NonNull GuildMemberRemoveEvent event, @NonNull String channelIdToSendTo) {

        Guild guild = event.getGuild();
        User user = event.getUser();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("🛫 Member Leave Event");
        eb.setDescription("A Member has left " + guild.getName());
        eb.setThumbnail(user.getEffectiveAvatarUrl());
        eb.setColor(Color.RED);

        eb.addField("🏷️ Member Name", "╰┈➤" + user.getName(), false);
        eb.addField("🆔 Member ID", "╰┈➤" + user.getId(), false);
        eb.addField("⌛ Member Joined The Server On", "╰┈➤" + getMemberJoinDate(event), false);
        eb.addField("⌛ Member Left The Server On", "╰┈➤" + getMemberLeaveDate(), false);

        eb.setFooter(event.getGuild().getName());
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

    @NotNull
    private static String getMemberJoinDate (@NonNull GuildMemberRemoveEvent event) {

        Member member = event.getMember();
        if(member==null)
            return "Member not cached";

        if (member.hasTimeJoined())
            return "<t:" + member.getTimeJoined().toEpochSecond() + ":f>";

        return "Unavailable";
    }

    @NotNull
    private static String getMemberLeaveDate () {
        return "<t:" + Instant.now().getEpochSecond() + ":f>";
    }
}
