package io.github.eggy03.papertrail.bot.handlers.auditlog;

import io.github.eggy03.papertrail.bot.listeners.auditlog.GuildAuditLogEntryCreateEventHandler;
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
public final class IntegrationEventHandler extends GuildAuditLogEntryCreateEventHandler {

    private final @NonNull AuditLogRegistrationClient client;

    @Inject
    public IntegrationEventHandler(@NonNull AuditLogRegistrationClient client) {
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
    public void onIntegrationCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredGuildChannel(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Integration Create Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Integration Created By: " + mentionableExecutor));
        eb.setColor(Color.PINK);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "type" -> eb.addField(MarkdownUtil.underline("Integration Type"), "╰┈➤" + newValue, false);
                case "name" -> eb.addField(MarkdownUtil.underline("Integration Name"), "╰┈➤" + newValue, false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Integration Create: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    @Override
    public void onIntegrationUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {

        log.warn("Integration Update Event Detected. Implement this sometime later\n{}", event.getEntry().getChanges());

        String channelIdToSendTo = getRegisteredGuildChannel(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Integration Update Event");
        eb.setColor(Color.LIGHT_GRAY);

        String implementationNotice = "We do not have sufficient data to log the changes in an INTEGRATION_UPDATE Event."
                .concat(" A proper implementation might happen in future releases if such an event is fired consistently.");

        eb.addField("Implementation Notice", MarkdownUtil.codeblock(implementationNotice), false);

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    @Override
    public void onIntegrationDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredGuildChannel(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Integration Delete Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Integration Deleted By: " + mentionableExecutor));
        eb.setColor(Color.MAGENTA);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "type" -> eb.addField(MarkdownUtil.underline("Integration Type"), "╰┈➤" + oldValue, false);
                case "name" -> eb.addField(MarkdownUtil.underline("Integration Name"), "╰┈➤" + oldValue, false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Integration Delete: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }

    @Override
    public void onApplicationCommandPrivilegesUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredGuildChannel(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Application Command Privilege Update");
        eb.setDescription(MarkdownUtil.quoteBlock("Application Command Privilege Updated By: " + mentionableExecutor));
        eb.setColor(Color.PINK);

        eb.addField(MarkdownUtil.underline("More Info"), MarkdownUtil.codeblock("To know more about what changes were made, visit the Integrations section"), false);

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb, channelIdToSendTo);
    }
}
