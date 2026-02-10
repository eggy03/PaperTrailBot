package io.github.eggy03.papertrail.bot.listeners.audit.helper.role;

import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.commons.utils.ColorUtils;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.role.utils.RoleUtils;
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
public class RoleDeleteEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());


        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Role Delete Event");
        eb.setDescription("ℹ️ The following role was deleted by: "+mentionableExecutor);
        eb.setColor(Color.RED);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "name"-> eb.addField("Role Name", "╰┈➤"+oldValue, false);

                case "hoist"-> eb.addField("Display Separately", "╰┈➤"+ BooleanUtils.formatToYesOrNo(oldValue), false);

                case "color" -> eb.addField("Color", "╰┈➤"+ ColorUtils.formatToHex(oldValue), false);

                case "permissions"-> eb.addField("Role Permissions", RoleUtils.resolveRolePermissions(oldValue, "✅"), false);

                case "mentionable"-> eb.addField("Mentionable", "╰┈➤"+ BooleanUtils.formatToYesOrNo(oldValue), false);

                case "colors" -> eb.addField("Gradient Color System", ColorUtils.formatGradientColorSystemToHex(oldValue), false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }

        });
        eb.addField("🆔 Deleted Role ID", "╰┈➤"+ale.getTargetId(), false);

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
