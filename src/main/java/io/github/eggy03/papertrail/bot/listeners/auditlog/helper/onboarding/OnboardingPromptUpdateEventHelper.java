package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.onboarding;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
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
public class OnboardingPromptUpdateEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Onboarding Prompt Update Event");

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Onboarding Prompt Updated By: " + mentionableExecutor));
        eb.setColor(Color.YELLOW);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "single_select" -> {
                    eb.addField(MarkdownUtil.underline("Old Single Selection Mode"), BooleanUtils.formatToYesOrNo(oldValue), false);
                    eb.addField(MarkdownUtil.underline("New Single Selection Mode"), BooleanUtils.formatToYesOrNo(newValue), false);
                }

                case "required" -> {
                    eb.addField(MarkdownUtil.underline("Was Required"), BooleanUtils.formatToYesOrNo(oldValue), false);
                    eb.addField(MarkdownUtil.underline("Is Required"), BooleanUtils.formatToYesOrNo(newValue), false);
                }

                case "type", "id" -> {
                    // skip
                }

                case "title" -> {
                    eb.addField(MarkdownUtil.underline("Old Question Title"), String.valueOf(oldValue), false);
                    eb.addField(MarkdownUtil.underline("New Question Title"), String.valueOf(newValue), false);
                }

                case "options" ->
                        eb.addField(MarkdownUtil.underline("Question Options"), "Review the changed options in Onboarding Settings", false);

                case "in_onboarding" -> {
                    eb.addField(MarkdownUtil.underline("Was a Pre-Join Question"), BooleanUtils.formatToYesOrNo(oldValue), false);
                    eb.addField(MarkdownUtil.underline("Is a Pre-Join Question"), BooleanUtils.formatToYesOrNo(newValue), false);
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
