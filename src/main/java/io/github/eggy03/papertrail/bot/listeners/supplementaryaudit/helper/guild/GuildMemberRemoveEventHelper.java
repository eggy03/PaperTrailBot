package io.github.eggy03.papertrail.bot.listeners.supplementaryaudit.helper.guild;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

import java.awt.Color;
import java.time.Instant;

@UtilityClass
public class GuildMemberRemoveEventHelper {

    public static void format(@NonNull GuildMemberRemoveEvent event, @NonNull String channelIdToSendTo) {

        Guild guild = event.getGuild();
        User user = event.getUser();
        Member member = event.getMember();

        String memberJoinDate = "Member Not Cached";
        boolean memberJoinDateTrustable = false;
        if (member != null) {
            memberJoinDate = "<t:" + member.getTimeJoined().toEpochSecond() + ":f>";
            memberJoinDateTrustable = member.hasTimeJoined();
        }
        String memberLeaveDate = "<t:" + Instant.now().getEpochSecond() + ":f>";

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("🛫 Member Leave Event");
        eb.setDescription("A Member has left " + guild.getName());
        eb.setThumbnail(user.getEffectiveAvatarUrl());
        eb.setColor(Color.RED);

        eb.addField("🏷️ Member Name", "╰┈➤" + user.getName(), false);
        eb.addField("🆔 Member ID", "╰┈➤" + user.getId(), false);
        eb.addField("⌛ Member Joined The Server On", "╰┈➤" + memberJoinDate, false);
        eb.addField("⌛ Member Left The Server On", "╰┈➤" + memberLeaveDate, false);
        eb.addField("⌛ Member Join Date Validity", memberJoinDateTrustable ? "╰┈➤Valid" : "╰┈➤Invalid", false);

        eb.setFooter("If the member was loaded via lazy loading, join date will be identical to the guild creation date.");
        eb.setTimestamp(Instant.now());

        MessageEmbed mb = eb.build();

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
