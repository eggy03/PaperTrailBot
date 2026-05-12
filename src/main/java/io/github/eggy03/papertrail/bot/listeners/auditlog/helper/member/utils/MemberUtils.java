package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.member.utils;

import io.github.eggy03.papertrail.bot.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/*
 * See {@link io.github.eggy03.papertrail.bot.utils.StackWalkerUtils}
 * and other utilities in the util package
 * to know why logging isn't needed in some of the methods here
 */
@UtilityClass
@Slf4j
public class MemberUtils {

    @NonNull
    public static final String FALLBACK_STRING = "N/A";

    // Parses an array list of map of role objects supplied by JDA
    // roles are exposed as arraylists of maps [{name=role, id=1}, {name=role2, id=2}]
    @NotNull
    public static String parseRoleListMap(@NonNull GenericGuildEvent event, @Nullable Object roleObject) {

        if (!(roleObject instanceof List<?> roleList) || roleList.isEmpty())
            return FALLBACK_STRING;

        return roleList.stream()
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(roleMap -> NumberParseUtils.parseLong(roleMap.get("id")))
                .filter(Objects::nonNull)
                .map(event.getGuild()::getRoleById)
                .filter(Objects::nonNull)
                .map(Role::getAsMention)
                .collect(Collectors.joining(" "));

    }
}
