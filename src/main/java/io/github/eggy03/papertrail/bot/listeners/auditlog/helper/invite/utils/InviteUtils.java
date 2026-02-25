package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.invite.utils;

import io.github.eggy03.papertrail.bot.commons.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

@UtilityClass
public class InviteUtils {

    @NotNull
    public static String resolveInviter(@Nullable Object inviterId, @NonNull GuildAuditLogEntryCreateEvent event) {

        if (inviterId == null)
            return "N/A";

        Member member = event.getGuild().getMemberById(inviterId.toString());

        return member != null ? member.getAsMention() : inviterId.toString();
    }

    @NotNull
    public static String resolveInviteChannel(@Nullable Object inviteChannelId, @NonNull GuildAuditLogEntryCreateEvent event) {

        if (inviteChannelId == null)
            return "N/A";

        GuildChannel channel = event.getGuild().getGuildChannelById(inviteChannelId.toString());

        return channel != null ? channel.getAsMention() : inviteChannelId.toString();
    }

    @NotNull
    public static String resolveMaxUses(@Nullable Object usageCountIntegerObject) {

        Integer maxUses = NumberParseUtils.parseInt(usageCountIntegerObject);

        if (maxUses == null)
            return "N/A";

        if (maxUses == 0)
            return "Unlimited";

        return maxUses.toString();
    }


}
