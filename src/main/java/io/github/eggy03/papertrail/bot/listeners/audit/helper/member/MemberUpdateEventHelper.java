package io.github.eggy03.papertrail.bot.listeners.audit.helper.member;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.commons.utils.DurationUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.Objects;

@UtilityClass
@Slf4j
public class MemberUpdateEventHelper {

    public static void format(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        User target = ale.getJDA().getUserById(ale.getTargetIdLong());
        String mentionableTarget = (target !=null ? target.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Member Update Event");
        eb.setDescription("ℹ️ The following member was updated by: "+mentionableExecutor);
        eb.setThumbnail(Objects.requireNonNull(event.getGuild().getMemberById(ale.getTargetId())).getEffectiveAvatarUrl());
        eb.setColor(Color.CYAN);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        eb.addField("Target", "╰┈➤"+mentionableTarget, false);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "communication_disabled_until" -> {
                    if(newValue==null) {
                        eb.setColor(Color.GREEN);
                        eb.addField("Timeout Lifted", "╰┈➤ Timeout has been removed", false);
                    } else {
                        eb.setColor(Color.YELLOW);
                        eb.addField("Timeout Received", "╰┈➤ Member has received a timeout", false);
                        eb.addField("Timeout Ends On", "╰┈➤"+ DurationUtils.isoToLocalTimeCounter(newValue), false);
                        eb.addField("Timeout Reason", "╰┈➤"+(ale.getReason()!=null ? ale.getReason() : "No Reason Provided"), false);
                    }
                }

                case "nick" -> eb.addField("Nickname Update", "╰┈➤"+resolveNickNameChanges(oldValue, newValue), false);

                case "mute" -> eb.addField("Is Muted in VC", BooleanUtils.formatToYesOrNo(newValue), false);

                case "deaf" -> eb.addField("Is Deafened in VC", BooleanUtils.formatToYesOrNo(newValue), false);

                case "bypasses_verification" -> eb.addField("Bypass Verification", BooleanUtils.formatToEnabledOrDisabled(newValue), false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();
        if(!mb.isSendable()){
            log.warn("Embed is empty or too long (current length: {}).", eb.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }

    @NotNull
    @SuppressWarnings("all")
    private static String resolveNickNameChanges(@Nullable Object oldNickValue, @Nullable Object newNickValue) {

        if(oldNickValue==null && newNickValue!=null) { // change from global name to a new nickname in the server
            return "New Nickname Added: `"+newNickValue+"`";
        }

        if (oldNickValue!=null && newNickValue==null) { // change to the global name from having a nickname
            return "Reset to Global Name from: `"+oldNickValue+"`";
        }

        if (oldNickValue!=null && newNickValue!=null) { // changing from one nick to another
            return "Changed nickname from: `"+oldNickValue+"` to: `"+newNickValue+"`";
        }

        // both shouldn't be null which indicates that names couldn't be fetched from the event
        return "Changes could not be resolved!";
    }
}
