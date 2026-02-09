package io.github.eggy03.papertrail.bot.listeners.audit.helper.role.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

@UtilityClass
public class RoleUtils {

    // INTERNAL HELPERS
    @Nullable
    private static Long parseLong(@Nullable Object possibleLongValue) {
        if (possibleLongValue == null) return null;
        try {
            return Long.parseLong(String.valueOf(possibleLongValue));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    private static Integer parseInt(@Nullable Object possibleIntegerValue) {
        if (possibleIntegerValue == null) return null;
        try {
            return Integer.parseInt(String.valueOf(possibleIntegerValue));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ROLE UTILS

    @NotNull
    public static String resolveRolePermissions (@Nullable Object permissionsValue, @NonNull String emoji) {
        Long permissionLong = parseLong(permissionsValue);
        if(permissionLong==null)
            return "Permission cannot be parsed";

        if(permissionLong==0)
            return "No Permissions set";

        StringBuilder permissions = new StringBuilder();
        EnumSet<Permission> permissionEnum = Permission.getPermissions(permissionLong);

        permissionEnum.forEach(permission -> permissions
                .append(emoji)
                .append(" ")
                .append(permission.getName())
                .append(System.lineSeparator())
        );

        return permissions.toString().trim();
    }
}
