package io.github.eggy03.papertrail.bot.listeners.audit.helper.automod;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.automod.utils.AutoModUtils;
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
public class AutoModerationRuleCreateEventHelper {

    public static void format (GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | AutoMod Rule Create");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription("ℹ️ The following AutoMod rule was created by: "+mentionableExecutor);
        eb.setColor(Color.GREEN);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        ale.getChanges().forEach((changeKey, changeValue)-> {

            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "enabled" -> eb.addField("Enabled", "╰┈➤"+ BooleanUtils.formatToYesOrNo(newValue), false);

                case "trigger_type" -> eb.addField("Trigger Type", "╰┈➤"+ AutoModUtils.autoModTriggerTypeResolver(newValue), false);

                case "event_type" -> eb.addField("Event Type", "╰┈➤"+ AutoModUtils.autoModEventTypeResolver(newValue), false);

                case "name" -> eb.addField("AutoMod Rule Name ", "╰┈➤"+newValue, false);

                default -> {
                    // ignore everything else
                }
            }
        });

        eb.addField("Additional Info", "For more info on trigger metadata, actions, exempt roles and channels, visit Safety Setup in your server", false);
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
}
