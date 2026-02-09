package io.github.eggy03.papertrail.bot.listeners.audit.helper.onboarding;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
@Slf4j
public class OnboardingPromptUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Onboarding Prompt Update Event");

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getTargetId());

        eb.setDescription("👤 " + mentionableExecutor + "has made changes to an existing Onboarding Prompt");
        eb.setColor(Color.YELLOW);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey){
                case "single_select" -> {
                    eb.addField("Old Single Selection Mode", BooleanUtils.formatToYesOrNo(oldValue), false);
                    eb.addField("New Single Selection Mode", BooleanUtils.formatToYesOrNo(newValue), false);
                }

                case "required" -> {
                    eb.addField("Was Required", BooleanUtils.formatToYesOrNo(oldValue), false);
                    eb.addField("Is Required", BooleanUtils.formatToYesOrNo(newValue), false);
                }

                case "type", "id" -> {
                    // skip
                }

                case "title" -> {
                    eb.addField("Old Question Title", String.valueOf(oldValue), false);
                    eb.addField("New Question Title", String.valueOf(newValue), false);
                }

                case "options" -> eb.addField("Question Options", "Review the changed options in Onboarding Settings", false);

                case "in_onboarding" -> {
                    eb.addField("Was a Pre-Join Question", BooleanUtils.formatToYesOrNo(oldValue), false);
                    eb.addField("Is a Pre-Join Question", BooleanUtils.formatToYesOrNo(newValue), false);
                }

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
            log.warn("An embed is either empty or has exceed the max length for characters, with current length: {}", eb.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
