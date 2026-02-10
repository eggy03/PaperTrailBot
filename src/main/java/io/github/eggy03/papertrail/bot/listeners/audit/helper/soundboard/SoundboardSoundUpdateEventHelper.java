package io.github.eggy03.papertrail.bot.listeners.audit.helper.soundboard;

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
public class SoundboardSoundUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Soundboard Sound Update Event");
        eb.setDescription("ℹ️ A sound item in the soundboard was updated by: "+mentionableExecutor);
        eb.setColor(Color.YELLOW);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);
        eb.addBlankField(true);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "user_id", "sound_id", "id", "guild_id", "available" ->{
                    // skip
                }
                case "volume" -> {
                    eb.addField("Old Volume", "╰┈➤"+oldValue, true);
                    eb.addField("New Volume", "╰┈➤"+newValue, true);
                    eb.addBlankField(true);
                }
                case "emoji_name" -> {
                    eb.addField("Old Related Emoji", "╰┈➤"+oldValue, true);
                    eb.addField("New Related Emoji", "╰┈➤"+newValue, true);
                    eb.addBlankField(true);
                }
                case "emoji_id" -> {
                    eb.addField("Old Related Emoji ID", "╰┈➤"+oldValue, true);
                    eb.addField("New Related Emoji ID", "╰┈➤"+newValue, true);
                    eb.addBlankField(true);
                }
                case "name" -> {
                    eb.addField("Old Sound Item Name", "╰┈➤"+oldValue, true);
                    eb.addField("New Sound Item Name", "╰┈➤"+newValue, true);
                    eb.addBlankField(true);
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
            log.warn("Embed is empty or too long (current length: {}).", eb.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(channelIdToSendTo);
        if(sendingChannel!=null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(mb).queue();
        }
    }
}
