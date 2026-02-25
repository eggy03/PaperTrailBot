package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.member;

import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.member.utils.MemberUtils;
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
public class MemberRoleUpdateEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        User targetUser = ale.getJDA().getUserById(ale.getTargetIdLong());
        String mentionableTargetUser = (targetUser != null ? targetUser.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry  | Member Role Update");
        eb.setDescription(MarkdownUtil.quoteBlock("Executor: " + mentionableExecutor + "\nTarget: " + mentionableTargetUser));
        eb.setColor(Color.YELLOW);

        if (targetUser != null)
            eb.setThumbnail(targetUser.getEffectiveAvatarUrl());

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "$add" ->
                        eb.addField(MarkdownUtil.underline("✅ Role(s) Added"), "╰┈➤" + MemberUtils.parseRoleListMap(event, newValue), false);

                case "$remove" ->
                        eb.addField(MarkdownUtil.underline("❌ Role(s) Removed"), "╰┈➤" + MemberUtils.parseRoleListMap(event, newValue), false);

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
