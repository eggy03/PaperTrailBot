package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.stage;

import io.github.eggy03.papertrail.bot.listeners.auditlog.helper.stage.utils.StageUtils;
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
public class StageInstanceUpdateEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Stage Instance Update Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Stage Instance Updated By: " + mentionableExecutor));
        eb.setColor(Color.YELLOW);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "topic" -> {
                    eb.addField(MarkdownUtil.underline("Old Stage Topic"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Stage Topic"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }
                case "privacy_level" -> {
                    eb.addField(MarkdownUtil.underline("Old Stage Privacy"), "╰┈➤" + StageUtils.resolveStagePrivacyLevel(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Stage Privacy"), "╰┈➤" + StageUtils.resolveStagePrivacyLevel(newValue), true);
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
