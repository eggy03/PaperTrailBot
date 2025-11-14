package org.papertrail.listeners.audit.helper;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;
import java.util.Objects;

@UtilityClass
public class AutoModerationRuleBlockMessageEventHelper {

    public static void format (GuildAuditLogEntryCreateEvent event, AuditLogEntry ale, String channelIdToSendTo) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Automod Event");

        User targetUser = ale.getJDA().getUserById(ale.getTargetId());
        String mentionableTargetUser = (targetUser != null ? targetUser.getAsMention() : ale.getTargetId());

        eb.setDescription("🛈 Automod has blocked a message sent by: "+mentionableTargetUser);
        eb.setColor(Color.ORANGE);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        eb.setFooter("Audit Log Entry ID: "+ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        MessageEmbed mb = eb.build();
        Objects.requireNonNull(event.getGuild().getTextChannelById(channelIdToSendTo)).sendMessageEmbeds(mb).queue();
    }
}
