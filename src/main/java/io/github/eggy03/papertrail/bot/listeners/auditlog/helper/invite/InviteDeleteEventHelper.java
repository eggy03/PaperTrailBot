package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.invite;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.invite.utils.InviteUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;

@UtilityClass
@Slf4j
public class InviteDeleteEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Invite Delete Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription(MarkdownUtil.quoteBlock("Invite Deleted By: " + mentionableExecutor));
        eb.setColor(Color.BLUE);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "code" -> eb.addField(MarkdownUtil.underline("Deleted Invite Code"), "╰┈➤" + oldValue, false);

                case "inviter_id" ->
                        eb.addField(MarkdownUtil.underline("Invite Deleted By"), "╰┈➤" + InviteUtils.resolveInviter(oldValue, event), false);

                case "temporary" ->
                        eb.addField(MarkdownUtil.underline("Was Temporary"), "╰┈➤" + BooleanUtils.formatToYesOrNo(oldValue), false);

                case "max_uses", "flags", "max_age" -> {
                    // ignore
                }

                case "uses" -> eb.addField(MarkdownUtil.underline("Use Count"), "╰┈➤" + oldValue, false);

                case "channel_id" ->
                        eb.addField(MarkdownUtil.underline("Invite Channel"), "╰┈➤" + InviteUtils.resolveInviteChannel(oldValue, event), false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }

            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

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
