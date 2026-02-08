package io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

@UtilityClass
public class ChannelUtils {

    @NotNull
    public static String resolvePermissions(@Nullable Object bitField, @NonNull String emoji) {
        if(bitField==null)
            return "Null Permission Bitfield";

        if(String.valueOf(bitField).trim().isEmpty())
            return "Permission Bitfield was blank";

        try {
            long permValue = Long.parseLong(String.valueOf(bitField));
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
            return "Permission bitfield could not be parsed: "+bitField;
        }
    }

    @NotNull
    public static String resolveMemberOrRole(@Nullable Object memberOrRoleId, @NonNull GuildAuditLogEntryCreateEvent event) {

        Member mb = event.getGuild().getMemberById(String.valueOf(memberOrRoleId));
        Role r = event.getGuild().getRoleById(String.valueOf(memberOrRoleId));

        if(mb!=null) {
           return mb.getAsMention();
        } else if (r!=null) {
            return r.getAsMention();
        }

        return String.valueOf(memberOrRoleId);
    }

    @NotNull
    public static String resolveChannelOverrideTargetType (@Nullable Object targetType) {
        if (targetType == null) {
            return "Null Target Type";
        }

        try {
            int typeInt = Integer.parseInt(targetType.toString());
            return switch (typeInt) {
                case 0 -> "Role";
                case 1 -> "Member/Application";
                default -> "Unknown Type: "+typeInt;
            };
        } catch (NumberFormatException e) {
            return "Target Type Cannot Be Parsed";
        }

    }
}
