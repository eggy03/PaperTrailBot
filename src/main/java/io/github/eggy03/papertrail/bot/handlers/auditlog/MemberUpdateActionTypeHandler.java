package io.github.eggy03.papertrail.bot.handlers.auditlog;

import io.github.eggy03.papertrail.bot.listeners.auditlog.GuildAuditLogEntryCreateEventActionTypeHandler;
import io.github.eggy03.papertrail.bot.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.utils.DurationUtils;
import io.github.eggy03.papertrail.bot.utils.auditlog.MemberUtils;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;

@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class MemberUpdateActionTypeHandler extends GuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull AuditLogRegistrationClient client;

    @Inject
    public MemberUpdateActionTypeHandler(@NonNull AuditLogRegistrationClient client) {
        this.client = client;
    }

    @NonNull
    private String getRegisteredChannelId(@NonNull String guildId) {
        return client.getRegisteredGuild(guildId)
                .map(AuditLogRegistrationEntity::getChannelId).orElse(StringUtils.EMPTY);

    }

    private void performChecksThenBuildAndSendEmbed(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull EmbedBuilder embedBuilder, @NonNull String channelIdToSendTo) {
        if (!embedBuilder.isValidLength() || embedBuilder.isEmpty()) {
            log.warn("Embed is empty or too long (current length: {}).", embedBuilder.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    @Override
    public void onMemberUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        User targetUser = ale.getJDA().getUserById(ale.getTargetIdLong());
        String mentionableTargetUser = (targetUser != null ? targetUser.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Member Update Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Executor: " + mentionableExecutor + "\nTarget: " + mentionableTargetUser));
        eb.setColor(Color.CYAN);

        if (targetUser != null)
            eb.setThumbnail(targetUser.getEffectiveAvatarUrl());

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "communication_disabled_until" -> {
                    if (newValue == null) {
                        eb.setColor(Color.GREEN);
                        eb.addField(MarkdownUtil.underline("Timeout Lifted"), "╰┈➤ Timeout has been removed", false);
                    } else {
                        eb.setColor(Color.YELLOW);
                        eb.addField(MarkdownUtil.underline("Timeout Received"), "╰┈➤ Member has received a timeout", false);
                        eb.addField(MarkdownUtil.underline("Timeout Ends On"), "╰┈➤" + DurationUtils.isoToLocalTimeCounter(newValue), false);
                        eb.addField(MarkdownUtil.underline("Timeout Reason"), "╰┈➤" + (ale.getReason() != null ? ale.getReason() : "No Reason Provided"), false);
                    }
                }
                case "nick" ->
                        eb.addField(MarkdownUtil.underline("Nickname Update"), MemberUtils.resolveNickNameChanges(oldValue, newValue), false);

                case "mute" ->
                        eb.addField(MarkdownUtil.underline("Is Muted in VC"), "╰┈➤" + BooleanUtils.formatToYesOrNo(newValue), false);

                case "deaf" ->
                        eb.addField(MarkdownUtil.underline("Is Deafened in VC"), "╰┈➤" + BooleanUtils.formatToYesOrNo(newValue), false);

                case "bypasses_verification" ->
                        eb.addField(MarkdownUtil.underline("Bypass Verification"), "╰┈➤" + BooleanUtils.formatToEnabledOrDisabled(newValue), false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Member Update: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    @Override
    public void onMemberRoleUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

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
                    log.info("Unimplemented Change Key on Member Role Update: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    // the audit log does not expose much information regarding member vc move and kick events
    // therefore GuildVoiceEventListener has been created to know about channels the target has been moved or kicked from
    @Override
    public void onMemberVoiceKick(@NonNull GuildAuditLogEntryCreateEvent event) {

        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Member Voice Kick Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Member Kicked From a Voice Channel\n By: " + mentionableExecutor));
        eb.setColor(Color.RED);

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    // the audit log does not expose much information regarding member vc move and kick events
    // therefore GuildVoiceEventListener has been created to know about channels the target has been moved or kicked from
    @Override
    public void onMemberVoiceMove(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Member Voice Move Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Member Moved To A Different Voice Channel\n By: " + mentionableExecutor));
        eb.setColor(Color.YELLOW);

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }
}
