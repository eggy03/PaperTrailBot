package io.github.eggy03.papertrail.bot.listeners.auditlogsupl.helper.guild;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.commons.utils.DurationUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;
import java.time.Instant;

@UtilityClass
@Slf4j
public class GuildMemberJoinEventHelper {

    public static void format(@NonNull GuildMemberJoinEvent event, @NonNull String channelIdToSendTo) {

        Guild guild = event.getGuild();
        User user = event.getUser();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Member Join Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Member Joined: " + user.getName() + "\nGuild: " + guild.getName()));
        eb.setThumbnail(user.getEffectiveAvatarUrl());
        eb.setColor(Color.GREEN);

        eb.addField(MarkdownUtil.underline("Member Name"), "╰┈➤" + user.getName(), false);
        eb.addField(MarkdownUtil.underline("Member Mention"), "╰┈➤" + user.getAsMention(), false);
        eb.addField(MarkdownUtil.underline("Member ID"), "╰┈➤" + user.getId(), false);
        eb.addField(MarkdownUtil.underline("Account Created"), "╰┈➤" + DurationUtils.isoToLocalTimeCounter(user.getTimeCreated()), false);
        eb.addField(MarkdownUtil.underline("Bot Account"), "╰┈➤" + BooleanUtils.formatToYesOrNo(user.isBot()), false);

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
}
