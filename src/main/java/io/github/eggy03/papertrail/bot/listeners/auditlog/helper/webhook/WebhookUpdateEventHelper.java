package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.webhook;

import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.guild.utils.GuildUtils;
import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.webhook.utils.WebhookUtils;
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
public class WebhookUpdateEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Webhook Update Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Webhook Updated By: " + mentionableExecutor));
        eb.setColor(Color.YELLOW);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "type" -> {
                    eb.addField(MarkdownUtil.underline("Old Webhook Type"), "╰┈➤" + WebhookUtils.resolveWebhookEventType(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Webhook Type"), "╰┈➤" + WebhookUtils.resolveWebhookEventType(newValue), true);
                    eb.addBlankField(true);
                }
                case "avatar_hash" ->
                        eb.addField(MarkdownUtil.underline("Avatar"), "╰┈➤Avatar has been updated", false);

                case "channel_id" -> {
                    eb.addField(MarkdownUtil.underline("Old Channel"), "╰┈➤" + GuildUtils.resolveMentionableChannel(oldValue, event), true);
                    eb.addField(MarkdownUtil.underline("New Channel"), "╰┈➤" + GuildUtils.resolveMentionableChannel(newValue, event), true);
                    eb.addBlankField(true);
                }
                case "name" -> {
                    eb.addField(MarkdownUtil.underline("Old Webhook Name"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Webhook Name"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }
                case "application_id" -> {
                    eb.addField(MarkdownUtil.underline("Old Application ID"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Application ID"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }
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
