package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.onboarding;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.onboarding.utils.OnboardingUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
@Slf4j
public class OnboardingUpdateEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Onboarding Update Event");

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getTargetId());

        Guild guild = event.getGuild();

        eb.setDescription("👤 " + mentionableExecutor + "has made changes to Onboarding");
        eb.setColor(Color.MAGENTA);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);
        eb.addBlankField(true);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "enabled" -> {
                    eb.addField("Old Onboarding Status", BooleanUtils.formatToEnabledOrDisabled(oldValue), true);
                    eb.addField("New Onboarding Status", BooleanUtils.formatToEnabledOrDisabled(newValue), true);
                    eb.addBlankField(true);
                }

                case "mode" -> {
                    eb.addField("Old Onboarding Mode", OnboardingUtils.formatMode(oldValue), true);
                    eb.addField("New Onboarding Mode", OnboardingUtils.formatMode(newValue), true);
                    eb.addBlankField(true);
                }

                case "default_channel_ids" -> {
                    eb.addField("Old Default Channels", OnboardingUtils.resolveChannelsFromList(guild, oldValue), true);
                    eb.addField("New Default Channels", OnboardingUtils.resolveChannelsFromList(guild, newValue), true);
                    eb.addBlankField(true);
                }

                // triggered also when prompts are deleted/created besides the default of update
                case "prompts" ->
                        eb.addField("Prompt Updates", "Overall Pre-join/Post-join questions may have been updated.\n Review changes manually.", false);

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
