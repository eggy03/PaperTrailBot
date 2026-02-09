package io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.utils;

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
        if (channelTypeInteger == null) {
            return "Channel Type Integer is Null";
        }

        try {
            int t = Integer.parseInt(channelTypeInteger.toString());
            return ChannelType.fromId(t).name();
        } catch (NumberFormatException e) {
            return "Channel Type Is Not Parsable: "+channelTypeInteger;
        }

    }

    @NotNull
    public static String resolveVoiceChannelUserLimit(Object limitNumber) {
        if (limitNumber == null) {
            return "User Limit was Null";
        }

        try {
            int limitNumberInt = Integer.parseInt(limitNumber.toString());
            return limitNumberInt==0 ? "Unlimited" : String.valueOf(limitNumberInt);
        } catch (NumberFormatException e) {
            return "User Limit Cannot Be Parsed";
        }

    }

    @NotNull
    public static String resolveVoiceChannelBitrate(@Nullable Object bitrate) {
        if (bitrate == null) {
            return "Voice Channel Bitrate was Null";
        }

        try {
            int bitrateInt = Integer.parseInt(bitrate.toString());
            return (bitrateInt / 1000) + " kbps";
        } catch (NumberFormatException e) {
            return "Bitrate could not be parsed: "+bitrate;
        }
    }

    @NotNull
    public static String resolveVoiceChannelVideoQuality(@Nullable Object voiceChannelVideoQualityInteger) {
        if (voiceChannelVideoQualityInteger == null) {
            return "Video Quality Integer was Null";
        }

        try {
            int videoQuality = Integer.parseInt(voiceChannelVideoQualityInteger.toString());
            return switch (videoQuality) {
                case 1 -> "Auto";
                case 2 -> "720p/full";
                default -> "Unknown Quality Mode: "+videoQuality;
            };
        } catch (NumberFormatException e) {
            return "Video Quality Integer Cannot Be Parsed";
        }
    }

    // CHANNEL OVERRIDE UTILS
    @NotNull
    public static String resolveChannelOverrideTargetType (@Nullable Object targetTypeInteger) {
        if (targetTypeInteger == null) {
            return "Null Target Type Integer";
        }

        try {
            int typeInt = Integer.parseInt(targetTypeInteger.toString());
            return switch (typeInt) {
                case 0 -> "Role";
                case 1 -> "Member/Application";
                default -> "Unknown Type: "+typeInt;
            };
        } catch (NumberFormatException e) {
            return "Target Type Integer Cannot Be Parsed: "+targetTypeInteger;
        }

    }

    @NotNull
    public static String resolveChannelOverridePermissions(@Nullable Object permissionValue, @NonNull String emoji) {
        if(permissionValue==null)
            return "Null Permission Value";

        if(String.valueOf(permissionValue).trim().isEmpty())
            return "Permission Value was blank";

        try {
            long permValue = Long.parseLong(String.valueOf(permissionValue));
            if(permValue==0) return "Permissions synced with category";

            StringBuilder permissions = new StringBuilder();
            EnumSet<Permission> permissionEnumSet = Permission.getPermissions(permValue);
            permissionEnumSet.forEach(permission -> permissions
                    .append(emoji)
                    .append(" ")
                    .append(permission.getName())
                    .append(System.lineSeparator()));

            return permissions.toString();

        } catch (NumberFormatException e){
            return "Permission Value could not be parsed: "+permissionValue;
        }
    }

    // MISC UTILS
    @NotNull
    public static String autoResolveMemberOrRole(@Nullable Object memberOrRoleId, @NonNull GuildAuditLogEntryCreateEvent event) {

        Member mb = event.getGuild().getMemberById(String.valueOf(memberOrRoleId));
        Role r = event.getGuild().getRoleById(String.valueOf(memberOrRoleId));

        if(mb!=null) {
            return mb.getAsMention();
        } else if (r!=null) {
            return r.getAsMention();
        }

        return String.valueOf(memberOrRoleId);
    }
}
