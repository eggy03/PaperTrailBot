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

    public static final String FALLBACK_STRING = "N/A";

    @NotNull
    public static String resolveInviter(@Nullable Object inviterId, @NonNull GuildAuditLogEntryCreateEvent event) {

        Long inviterIdLong = NumberParseUtils.parseLong(inviterId);
        if (inviterIdLong == null)
            return FALLBACK_STRING;

        Member member = event.getGuild().getMemberById(inviterIdLong);
        return member != null ? member.getAsMention() : inviterIdLong.toString();
    }

    @NotNull
    public static String resolveInviteChannel(@Nullable Object inviteChannelId, @NonNull GuildAuditLogEntryCreateEvent event) {

        Long inviteChannelIdLong = NumberParseUtils.parseLong(inviteChannelId);
        if (inviteChannelIdLong == null)
            return FALLBACK_STRING;

        GuildChannel channel = event.getGuild().getGuildChannelById(inviteChannelIdLong);
        return channel != null ? channel.getAsMention() : inviteChannelIdLong.toString();
    }

    @NotNull
    public static String resolveMaxUses(@Nullable Object usageCountIntegerObject) {

        Integer maxUses = NumberParseUtils.parseInt(usageCountIntegerObject);

        if (maxUses == null)
            return FALLBACK_STRING;

        if (maxUses == 0)
            return "Unlimited";

        return maxUses.toString();
    }


}
