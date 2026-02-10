package io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.utils;

import io.github.eggy03.papertrail.bot.commons.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

@UtilityClass
public class ChannelUtils {

    // CHANNEL UTILS
    @NotNull
    public static String resolveChannelType(@Nullable Object channelTypeInteger) {

        Integer channelType = NumberParseUtils.parseInt(channelTypeInteger);
        if (channelType == null)
            return "Channel Type cannot be parsed: " + channelTypeInteger;

        return ChannelType.fromId(channelType).name();
    }

    @NotNull
    public static String resolveVoiceChannelUserLimit(Object limitNumber) {
        Integer userLimit = NumberParseUtils.parseInt(limitNumber);
        if (userLimit == null)
            return "User Limit cannot be parsed";

        return userLimit == 0 ? "Unlimited" : userLimit.toString();
    }

    @NotNull
    public static String resolveVoiceChannelBitrate(@Nullable Object bitrateInteger) {
        Integer bitrate = NumberParseUtils.parseInt(bitrateInteger);
        if (bitrate == null)
            return "Voice Channel Bitrate cannot be parsed";

        return (bitrate / 1000) + " kbps";
    }

    @NotNull
    public static String resolveVoiceChannelVideoQuality(@Nullable Object voiceChannelVideoQualityInteger) {

        Integer videoQuality = NumberParseUtils.parseInt(voiceChannelVideoQualityInteger);

        return switch (videoQuality) {
            case 1 -> "Auto";
            case 2 -> "720p/full";
            case null -> "Video Quality cannot be parsed";
            default -> "Unknown Quality Mode: " + videoQuality;
        };
    }

    // CHANNEL OVERRIDE UTILS
    @NotNull
    public static String resolveChannelOverrideTargetType(@Nullable Object targetTypeInteger) {
        Integer targetType = NumberParseUtils.parseInt(targetTypeInteger);

        return switch (targetType) {
            case 0 -> "Role";
            case 1 -> "Member/Application";
            case null -> "Target Type cannot be parsed";
            default -> "Unknown Type: " + targetType;
        };

    }

    @NotNull
    public static String resolveChannelOverridePermissions(@Nullable Object permissionValueLong, @NonNull String emoji) {

        Long permissionValue = NumberParseUtils.parseLong(permissionValueLong);
        if (permissionValue == null)
            return "Permission Value cannot be parsed";

        if (permissionValue == 0)
            return "Permissions synced with category";

        StringBuilder permissions = new StringBuilder();
        EnumSet<Permission> permissionEnumSet = Permission.getPermissions(permissionValue);
        permissionEnumSet.forEach(permission -> permissions
                .append(emoji)
                .append(" ")
                .append(permission.getName())
                .append(System.lineSeparator()));

        return permissions.toString().trim();
    }

    // MISC UTILS
    @NotNull
    public static String autoResolveMemberOrRole(@Nullable Object memberOrRoleId, @NonNull GuildAuditLogEntryCreateEvent event) {

        if (memberOrRoleId == null)
            return "Member/Role ID cannot be parsed";

        Member mb = event.getGuild().getMemberById(String.valueOf(memberOrRoleId));
        Role r = event.getGuild().getRoleById(String.valueOf(memberOrRoleId));

        if (mb != null) {
            return mb.getAsMention();
        } else if (r != null) {
            return r.getAsMention();
        }

        return String.valueOf(memberOrRoleId);
    }
}
