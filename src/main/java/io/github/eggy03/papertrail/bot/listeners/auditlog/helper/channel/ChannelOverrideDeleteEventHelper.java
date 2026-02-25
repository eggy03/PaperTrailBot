package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.channel;

import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.channel.utils.ChannelUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;

@UtilityClass
@Slf4j
public class ChannelOverrideDeleteEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

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
