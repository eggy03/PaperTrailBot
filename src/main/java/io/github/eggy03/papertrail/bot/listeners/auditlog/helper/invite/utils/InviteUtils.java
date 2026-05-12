package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.invite.utils;

import io.github.eggy03.papertrail.bot.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    // the object is an array of Role IDs
    @NotNull
    public static String resolveInviteRoleList(@NonNull GenericGuildEvent event, @Nullable Object inviteRoleObject) {

        if (!(inviteRoleObject instanceof List<?> inviteRoleList) || inviteRoleList.isEmpty())
            return FALLBACK_STRING;

        return inviteRoleList
                .stream()
                .map(NumberParseUtils::parseLong)
                .filter(Objects::nonNull)
                .map(event.getGuild()::getRoleById)
                .filter(Objects::nonNull)
                .map(Role::getAsMention)
                .collect(Collectors.joining(" "))
                .trim();
    }

}
