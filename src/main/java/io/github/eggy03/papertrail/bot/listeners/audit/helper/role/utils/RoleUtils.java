package io.github.eggy03.papertrail.bot.listeners.audit.helper.role.utils;

import io.github.eggy03.papertrail.bot.commons.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

@UtilityClass
public class RoleUtils {

    @NotNull
    public static String resolveRolePermissions(@Nullable Object permissionsValue, @NonNull String emoji) {
        Long permissionLong = NumberParseUtils.parseLong(permissionsValue);
        if (permissionLong == null)
            return "Permission cannot be parsed";

        if (permissionLong == 0)
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
