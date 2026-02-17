package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.invite;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
@Slf4j
public class InviteDeleteEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Invite Delete Event");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription("ℹ️ The following invite has been deleted by: " + mentionableExecutor);
        eb.setColor(Color.BLUE);
        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "code" -> eb.addField("Deleted Invite Code", "╰┈➤" + oldValue, false);

                case "inviter_id" -> {
                    User inviter = ale.getJDA().getUserById(String.valueOf(oldValue));
                    eb.addField("Invite Originally Created By", "╰┈➤" + (inviter != null ? inviter.getAsMention() : "`Unknown`"), false);
                }

                case "temporary" ->
                        eb.addField("Temporary Invite", "╰┈➤" + BooleanUtils.formatToYesOrNo(oldValue), false);

                case "max_uses", "flags", "max_age" -> {
                    // ignore
                }

                case "uses" -> eb.addField("Number of times the invite was used", "╰┈➤" + oldValue, false);

                case "channel_id" -> {
                    Channel channel = ale.getGuild().getGuildChannelById(String.valueOf(oldValue));
                    eb.addField("Invite Channel", "╰┈➤" + (channel != null ? channel.getAsMention() : "`" + oldValue + "`"), false);
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
