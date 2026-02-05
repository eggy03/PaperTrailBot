package org.papertrail.listeners.audit.helper;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.papertrail.commons.utilities.BooleanFormatter;

import java.awt.Color;

@UtilityClass
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
                case "single_select":
                    eb.addField("Old Single Selection Mode", BooleanFormatter.formatToYesOrNo(oldValue), false);
                    eb.addField("New Single Selection Mode", BooleanFormatter.formatToYesOrNo(newValue), false);
                    break;

                case "required":
                    eb.addField("Was Required", BooleanFormatter.formatToYesOrNo(oldValue), false);
                    eb.addField("Is Required", BooleanFormatter.formatToYesOrNo(newValue), false);
                    break;

                case "type", "id":
                    break;

                case "title":
                    eb.addField("Old Question Title", String.valueOf(oldValue), false);
                    eb.addField("New Question Title", String.valueOf(newValue), false);
                    break;

                case "options":
                    eb.addField("Question Options", "Review the changed options in Onboarding Settings", false);
                    break;

                case "in_onboarding":
                    eb.addField("Was a Pre-Join Question", BooleanFormatter.formatToYesOrNo(oldValue), false);
                    eb.addField("Is a Pre-Join Question", BooleanFormatter.formatToYesOrNo(newValue), false);
                    break;

                default:
                    eb.addField(changeKey, "OLD_VALUE: "+oldValue, false);
                    eb.addField(changeKey, "NEW_VALUE: "+newValue, false);
            }

        });

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
