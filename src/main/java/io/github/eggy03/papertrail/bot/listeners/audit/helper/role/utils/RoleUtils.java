package io.github.eggy03.papertrail.bot.listeners.audit.helper.role.utils;

import io.github.eggy03.papertrail.bot.commons.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.IllegalFormatException;
import java.util.Map;

/*
 * See {@link io.github.eggy03.papertrail.bot.commons.utils.StackWalkerUtils}
 * and other utilities in the util package
 * to know why logging isn't needed in some of the methods here
 */
@UtilityClass
@Slf4j
public class RoleUtils {

    private static final String FALLBACK_STRING = "N/A";

    @NotNull
    public static String resolveRolePermissions(@Nullable Object permissionsValue, @NonNull String emoji) {
        Long permissionLong = NumberParseUtils.parseLong(permissionsValue);
        if (permissionLong == null)
            return FALLBACK_STRING;

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

    public static String formatToHex(@Nullable Object colorValueInteger) {

        if (colorValueInteger == null) {
            log.debug("colorValueInteger is null");
            return FALLBACK_STRING;
        }

        String colorValueString = String.valueOf(colorValueInteger);

        try {
            int color = Integer.parseInt(colorValueString);
            return String.format("#%06X", color);
        } catch (NumberFormatException e) {
            log.debug("failed to parse color integer from value={})", colorValueInteger);
            return colorValueString;
        } catch (IllegalFormatException e) {
            log.debug("failed to format color integer to hex, value={})", colorValueInteger);
            return colorValueString;
        }
    }

    // gradient is returned as a hash map in the following structure
    // {"primary_color" = 123456789, "secondary_color" = 123456789, "tertiary_color" = 123456789}
    public static String formatGradientToHex(@Nullable Object gradientMap) {

        if (gradientMap == null) {
            log.debug("color gradient map is null");
            return FALLBACK_STRING;
        }

        if (gradientMap instanceof Map<?, ?> colorMap) {

            String primaryColor = "Primary Color: " + formatToHex(colorMap.get("primary_color"));
            String secondaryColor = "Secondary Color: " + formatToHex(colorMap.get("secondary_color"));
            String tertiaryColor = "Tertiary Color: " + formatToHex(colorMap.get("tertiary_color"));

            return primaryColor + System.lineSeparator() + secondaryColor + System.lineSeparator() + tertiaryColor;
        }

        log.debug("gradient value is not a Map, value={}", gradientMap);
        return gradientMap.toString();
    }
}
