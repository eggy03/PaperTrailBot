package io.github.eggy03.papertrail.bot.handlers.auditlog;

import io.github.eggy03.papertrail.bot.listeners.auditlog.GuildAuditLogEntryCreateEventHandler;
import io.github.eggy03.papertrail.bot.utils.auditlog.ChannelUtils;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;

@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class ChannelOverrideEventHandler extends GuildAuditLogEntryCreateEventHandler {

    private final @NonNull AuditLogRegistrationClient client;

    @Inject
    public ChannelOverrideEventHandler(@NonNull AuditLogRegistrationClient client) {
        this.client = client;
    }

    @NonNull
    private String getRegisteredGuildChannel(@NonNull String guildId) {
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
    public void onChannelOverrideCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredGuildChannel(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());
        String targetChannelMention = (targetChannel != null ? targetChannel.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Channel Override Create");
        eb.setDescription(MarkdownUtil.quoteBlock("Override Created By: " + mentionableExecutor + "\nTarget Channel: " + targetChannelMention));
        eb.setColor(Color.GREEN);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "type" ->
                        eb.addField(MarkdownUtil.underline("Override Type"), "╰┈➤" + ChannelUtils.resolveChannelOverrideTargetType(newValue), false);

                case "deny" ->
                        eb.addField(MarkdownUtil.underline("Denied Permissions"), ChannelUtils.resolveChannelOverridePermissions(newValue, "❌"), false);
                case "allow" ->
                        eb.addField(MarkdownUtil.underline("Allowed Permissions"), ChannelUtils.resolveChannelOverridePermissions(newValue, "✅"), false);

                // id exposes the member/role id which for which the channel permissions are overridden
                // only one member/role permission id is fetched per loop
                case "id" ->
                        eb.addField(MarkdownUtil.underline("Target Member/Role"), "╰┈➤" + ChannelUtils.autoResolveMemberOrRole(newValue, event), false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key for Channel Override Create: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());


        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    @Override
    public void onChannelOverrideUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredGuildChannel(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());
        String targetChannelMention = (targetChannel != null ? targetChannel.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Channel Override Update");
        eb.setDescription(MarkdownUtil.quoteBlock("Override Updated By: " + mentionableExecutor + "\nTarget Channel: " + targetChannelMention));
        eb.setColor(Color.YELLOW);

        eb.addField(MarkdownUtil.underline("Override Type"), "╰┈➤" + ChannelUtils.resolveChannelOverrideTargetType(ale.getOptionByName("type")), false);
        eb.addField(MarkdownUtil.underline("Permissions Overridden For"), "╰┈➤" + getTargetRoleOrMember(ale, event), false);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "deny" ->
                        eb.addField(MarkdownUtil.underline("Denied Permissions"), ChannelUtils.resolveChannelOverridePermissions(newValue, "❌"), false);
                case "allow" ->
                        eb.addField(MarkdownUtil.underline("Allowed Permissions"), ChannelUtils.resolveChannelOverridePermissions(newValue, "✅"), false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key for Channel Override Update: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }

            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    @Override
    public void onChannelOverrideDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredGuildChannel(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        GuildChannel targetChannel = event.getGuild().getGuildChannelById(ale.getTargetId());
        String targetChannelMention = (targetChannel != null ? targetChannel.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Channel Override Delete");

        eb.setDescription(MarkdownUtil.quoteBlock("Override Deleted By: " + mentionableExecutor + "\nTarget Channel: " + targetChannelMention));
        eb.setColor(Color.RED);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "type" ->
                        eb.addField(MarkdownUtil.underline("Override Type"), "╰┈➤" + ChannelUtils.resolveChannelOverrideTargetType(oldValue), false);

                case "deny" ->
                        eb.addField(MarkdownUtil.underline("Denied Permissions"), ChannelUtils.resolveChannelOverridePermissions(oldValue, "❌"), false);
                case "allow" ->
                        eb.addField(MarkdownUtil.underline("Allowed Permissions"), ChannelUtils.resolveChannelOverridePermissions(oldValue, "✅"), false);

                // id exposes the member/role id which for which the channel permissions are overridden
                // only one member/role permission id is fetched per loop
                case "id" ->
                        eb.addField(MarkdownUtil.underline("Target Member/Role"), "╰┈➤" + ChannelUtils.autoResolveMemberOrRole(oldValue, event), false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key for Channel Override Delete: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    // ALE changes do not expose the id and type keys in case of override updates
    // therefore, this hacky method is required
    // IDK why channel override updates don't show this info
    @NonNull
    private String getTargetRoleOrMember(@NonNull AuditLogEntry ale, @NonNull GuildAuditLogEntryCreateEvent event) {

        String overriddenId = ale.getOptionByName("id"); // id of the member or role
        String overriddenType = ale.getOptionByName("type"); // type that determines if the ID is that of a role or a member

        if (overriddenId == null)
            return "Member or Role ID is null";

        return switch (overriddenType) {

            case "0" -> {
                // It’s a role
                Role role = event.getGuild().getRoleById(overriddenId);
                yield role != null ? role.getAsMention() : overriddenId;
            }

            case "1" -> {
                // It's a member
                Member member = event.getGuild().getMemberById(overriddenId);
                yield member != null ? member.getAsMention() : overriddenId;
            }

            case null -> "Override Type is null";
            default -> "Unimplemented Override Type: " + overriddenType;

        };
    }
}
