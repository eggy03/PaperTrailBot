package io.github.eggy03.papertrail.bot.listeners.audit.helper.member;

import io.github.eggy03.papertrail.bot.commons.utilities.RoleObjectParser;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.awt.Color;
import java.util.Map;
import java.util.Objects;

@UtilityClass
@Slf4j
public class MemberRoleUpdateEventHelper {

    public static void format(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry  | Member Role Update");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        User target = ale.getJDA().getUserById(ale.getTargetIdLong());

        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());
        String mentionableTarget = (target !=null ? target.getAsMention() : ale.getTargetId());

        eb.setDescription("👤 **By**: "+mentionableExecutor+"\nℹ️ The following member role was updated");
        eb.setThumbnail(Objects.requireNonNull(event.getGuild().getMemberById(ale.getTargetIdLong())).getEffectiveAvatarUrl());
        eb.setColor(Color.YELLOW);

        eb.addField("Action Type", String.valueOf(ale.getType()), true);
        eb.addField("Target Type", String.valueOf(ale.getTargetType()), true);

        for(Map.Entry<String, AuditLogChange> changes: ale.getChanges().entrySet()) {

            String change = changes.getKey();
            Object oldValue = changes.getValue().getOldValue();
            Object newValue = changes.getValue().getNewValue();

            switch(change) {

                case "$add":
                    eb.addField("🏷️Target Member", "╰┈➤"+mentionableTarget, false);
                    eb.addField("✅Role(s) Added", "╰┈➤"+RoleObjectParser.parseRole(event, newValue), false);
                    break;

                case "$remove":
                    eb.addField("🏷️Target Member", "╰┈➤"+mentionableTarget, false);
                    eb.addField("❌Role(s) Removed", "╰┈➤"+RoleObjectParser.parseRole(event, newValue), false);
                    break;

                default:
                    eb.addField(change, "from "+oldValue+" to "+newValue, false);
            }
        }

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
