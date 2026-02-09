package io.github.eggy03.papertrail.bot.listeners.audit.helper.onboarding;

import io.github.eggy03.papertrail.bot.listeners.audit.helper.onboarding.utils.OnboardingUtils;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;

@UtilityClass
public class OnboardingUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

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

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey){
                case "enabled":
                    eb.addField("Old Onboarding Status", OnboardingUtils.formatStatus(oldValue), false);
                    eb.addField("New Onboarding Status", OnboardingUtils.formatStatus(newValue), true);
                    break;

                case "mode":
                    eb.addField("Old Onboarding Mode", OnboardingUtils.formatMode(oldValue), false);
                    eb.addField("New Onboarding Mode", OnboardingUtils.formatMode(newValue), true);
                    break;

                case "default_channel_ids":
                    eb.addField("Old Default Channels", OnboardingUtils.resolveChannelsFromList(guild, oldValue), false);
                    eb.addField("New Default Channels", OnboardingUtils.resolveChannelsFromList(guild, newValue), false);
                    break;

                case "prompts": // unknown as to exactly why this is triggered
                    eb.addField("Prompt Updates", "Overall Pre-join/Post-join questions may have been updated.\n Review changes manually.", false);
                    break;

                default:
                    eb.addField(changeKey, "from: "+oldValue+" to: "+newValue, false);
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
