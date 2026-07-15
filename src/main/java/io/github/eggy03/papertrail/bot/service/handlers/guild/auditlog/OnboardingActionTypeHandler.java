package io.github.eggy03.papertrail.bot.service.handlers.guild.auditlog;

import io.github.eggy03.papertrail.bot.service.EmbedCheckingService;
import io.github.eggy03.papertrail.bot.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.utils.auditlog.OnboardingUtils;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;

@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class OnboardingActionTypeHandler extends AbstractGuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull AuditLogRegistrationClient client;
    private final @NonNull EmbedCheckingService embedCheckingService;

    @Inject
    public OnboardingActionTypeHandler(@NonNull AuditLogRegistrationClient client, @NonNull EmbedCheckingService embedCheckingService) {
        this.client = client;
        this.embedCheckingService = embedCheckingService;
    }

    @NonNull
    private String getRegisteredChannelId(@NonNull String guildId) {
        return client.getRegisteredGuild(guildId)
                .map(AuditLogRegistrationEntity::getChannelId).orElse(StringUtils.EMPTY);

    }


    @Override
    public void onOnboardingCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
        log.warn("Onboarding Create Event Detected. Implement this sometime later\n{}", event.getEntry().getChanges());

        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Onboarding Create Event");
        eb.setColor(Color.LIGHT_GRAY);

        String implementationNotice = "We do not have sufficient data to log the changes in an ONBOARDING_CREATE Event."
                .concat(" That is because, even for Onboarding creations, ONBOARDING_UPDATE is fired instead.")
                .concat(" A proper implementation might happen in future releases if such an event is fired consistently.");

        eb.addField("Implementation Notice", MarkdownUtil.codeblock(implementationNotice), false);

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }

    @Override
    public void onOnboardingUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Onboarding Update Event");

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getTargetId());

        Guild guild = event.getGuild();

        eb.setDescription(MarkdownUtil.quoteBlock("Onboarding Settings Updated By: " + mentionableExecutor));
        eb.setColor(Color.MAGENTA);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "enabled" -> {
                    eb.addField(MarkdownUtil.underline("Old Onboarding Status"), BooleanUtils.formatToEnabledOrDisabled(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Onboarding Status"), BooleanUtils.formatToEnabledOrDisabled(newValue), true);
                    eb.addBlankField(true);
                }

                case "mode" -> {
                    eb.addField(MarkdownUtil.underline("Old Onboarding Mode"), OnboardingUtils.formatMode(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Onboarding Mode"), OnboardingUtils.formatMode(newValue), true);
                    eb.addBlankField(true);
                }

                case "default_channel_ids" -> {
                    eb.addField(MarkdownUtil.underline("Old Default Channels"), OnboardingUtils.resolveChannelsFromList(guild, oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Default Channels"), OnboardingUtils.resolveChannelsFromList(guild, newValue), true);
                    eb.addBlankField(true);
                }

                // triggered also when prompts are deleted/created besides the default of update
                case "prompts" ->
                        eb.addField(MarkdownUtil.underline("Prompt Updates"), "Overall Pre-join/Post-join questions may have been updated.\n Review changes manually.", false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Onboarding Update: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }

        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }
}
