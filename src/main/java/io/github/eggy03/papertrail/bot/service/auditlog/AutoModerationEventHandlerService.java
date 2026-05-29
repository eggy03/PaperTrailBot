package io.github.eggy03.papertrail.bot.service.auditlog;

import io.github.eggy03.papertrail.bot.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.utils.auditlog.AutoModUtils;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;

@ApplicationScoped
@Slf4j
public class AutoModerationEventHandlerService {

    public void handleFlagToChannelEvent(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Auto-Mod Event");

        User targetUser = ale.getJDA().getUserById(ale.getTargetId());
        String targetMention = (targetUser != null ? targetUser.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Event: AutoMod Message Flag\nTarget Member: " + targetMention));
        eb.setColor(Color.YELLOW);

        eb.addField(
                MarkdownUtil.underline("Additional Info"),
                MarkdownUtil.codeblock("Flagged message will be available in the channel set to receive AutoMod events."),
                false
        );

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

    public void handleMemberTimeoutEvent(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Auto-mod Event");

        User targetUser = ale.getJDA().getUserById(ale.getTargetId());
        String targetMention = (targetUser != null ? targetUser.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Event: AutoMod Member Timeout\nTarget Member: " + targetMention));
        eb.setColor(Color.MAGENTA);

        eb.addField(
                MarkdownUtil.underline("Additional Info"),
                MarkdownUtil.codeblock("Timeout Rule and Reason will be available in the channel set to receive AutoMod events."),
                false
        );

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

    public void handleRuleBlockMessageEvent(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Auto-mod Event");

        User targetUser = ale.getJDA().getUserById(ale.getTargetId());
        String targetMention = (targetUser != null ? targetUser.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Event: AutoMod Message Block\nTarget Member: " + targetMention));
        eb.setColor(Color.ORANGE);

        eb.addField(
                MarkdownUtil.underline("Additional Info"),
                MarkdownUtil.codeblock("Blocked message will be available in the channel set to receive AutoMod events."),
                false
        );

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

    public void handleRuleCreateEvent(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | AutoMod Rule Create");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription(MarkdownUtil.quoteBlock("Rule Created By: " + mentionableExecutor + "\nRule Created For: AutoMod"));
        eb.setColor(Color.GREEN);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "enabled" ->
                        eb.addField(MarkdownUtil.underline("Enabled"), "╰┈➤" + BooleanUtils.formatToYesOrNo(newValue), false);

                case "trigger_type" ->
                        eb.addField(MarkdownUtil.underline("Trigger Type"), "╰┈➤" + AutoModUtils.autoModTriggerTypeResolver(newValue), false);

                case "event_type" ->
                        eb.addField(MarkdownUtil.underline("Event Type"), "╰┈➤" + AutoModUtils.autoModEventTypeResolver(newValue), false);

                case "name" -> eb.addField(MarkdownUtil.underline("AutoMod Rule Name "), "╰┈➤" + newValue, false);

                default -> {
                    // ignore everything else
                }
            }
        });

        eb.addField("Additional Info", MarkdownUtil.codeblock("For more info on trigger metadata, actions, exempt roles and channels, visit Safety Setup in your server"), false);
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

    public void handleRuleDeleteEvent(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | AutoMod Rule Delete");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription(MarkdownUtil.quoteBlock("Rule Deleted By: " + mentionableExecutor + "\nRule Deleted For: AutoMod"));
        eb.setColor(Color.RED);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();

            if (changeKey.equals("name")) {
                eb.addField(MarkdownUtil.underline("AutoMod Rule Name"), "╰┈➤" + oldValue, false);
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

    public void handleRuleUpdateEvent(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | AutoMod Rule Update");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription(MarkdownUtil.quoteBlock("Rule Updated By: " + mentionableExecutor + "\nRule Updated For: AutoMod"));
        eb.setColor(Color.YELLOW);

        // add name of the rule which got updated
        AutoModRule rule = ale.getGuild().retrieveAutoModRuleById(ale.getTargetId()).complete();
        eb.addField(MarkdownUtil.underline("AutoMod Rule Name"), "╰┈➤" + rule.getName(), false);

        eb.addField(MarkdownUtil.underline("Additional Info"), MarkdownUtil.codeblock("For more info on trigger metadata, actions, exempt roles and channel changes, visit Safety Setup in your server"), false);

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
