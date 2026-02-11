package io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.utils;

import io.github.eggy03.papertrail.bot.commons.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

@UtilityClass
public class ChannelUtils {

    public static final String FALLBACK_STRING = "N/A";
    
    @NotNull
    public static String resolveChannelType(@Nullable Object channelTypeInteger) {

        Integer channelType = NumberParseUtils.parseInt(channelTypeInteger);
        if (channelType == null)
            return FALLBACK_STRING;

        return ChannelType.fromId(channelType).name();
    }

    @NotNull
    public static String resolveVoiceChannelUserLimit(@Nullable Object limitNumber) {
        Integer userLimit = NumberParseUtils.parseInt(limitNumber);
        if (userLimit == null)
            return FALLBACK_STRING;

        return userLimit == 0 ? "Unlimited" : userLimit.toString();
    }

    @NotNull
    public static String resolveVoiceChannelBitrate(@Nullable Object bitrateInteger) {
        Integer bitrate = NumberParseUtils.parseInt(bitrateInteger);
        if (bitrate == null)
            return FALLBACK_STRING;

        return (bitrate / 1000) + " kbps";
    }

    @NotNull
    public static String resolveVoiceChannelVideoQuality(@Nullable Object voiceChannelVideoQualityInteger) {

        Integer videoQuality = NumberParseUtils.parseInt(voiceChannelVideoQualityInteger);

        return switch (videoQuality) {
            case 1 -> "Auto";
            case 2 -> "720p/full";
            case null -> FALLBACK_STRING;
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
            case null -> FALLBACK_STRING;
            default -> "Unknown Type: " + targetType;
        };

    }

    @NotNull
    public static String resolveChannelOverridePermissions(@Nullable Object permissionValueLong, @NonNull String emoji) {

        Long permissionValue = NumberParseUtils.parseLong(permissionValueLong);
        if (permissionValue == null)
            return FALLBACK_STRING;

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
    public static String autoResolveMemberOrRole(@Nullable Object memberOrRoleId, @NonNull GenericGuildEvent event) {

        if (memberOrRoleId == null)
            return FALLBACK_STRING;

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
