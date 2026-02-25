package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.generic;

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

// for events not defined by JDA yet or not implemented by papertrail
@UtilityClass
@Slf4j
public class GenericAuditLogEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = executor != null ? executor.getAsMention() : ale.getUserId();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Generic Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Executor: " + mentionableExecutor));
        eb.setColor(Color.LIGHT_GRAY);

        eb.addField(MarkdownUtil.underline("Action Type"), MarkdownUtil.codeblock(ale.getType().toString()), true);
        eb.addField(MarkdownUtil.underline("Target Type"), MarkdownUtil.codeblock(ale.getTargetType().toString()), true);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            eb.addField(MarkdownUtil.underline(changeKey), MarkdownUtil.codeblock("OLD VALUE\n" + changeValue.getOldValue()), false);
            eb.addField(MarkdownUtil.underline(changeKey), MarkdownUtil.codeblock("NEW VALUE\n" + changeValue.getNewValue()), false);
        });

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
