package io.github.eggy03.papertrail.bot.utils.auditlog;

import io.github.eggy03.papertrail.bot.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public final class MemberUtils {

    @NonNull
    public static final String FALLBACK_STRING = "N/A";

    // Parses an array list of map of role objects supplied by JDA
    // roles are exposed as arraylists of maps [{name=role, id=1}, {name=role2, id=2}]
    @NonNull
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

    @NonNull
    @SuppressWarnings("all")
    public static String resolveNickNameChanges(@Nullable Object oldNickValue, @Nullable Object newNickValue) {

        if (oldNickValue == null && newNickValue != null) { // change from global name to a new nickname in the server
            return "Added Nickname: " + MarkdownUtil.underline(newNickValue.toString());
        }

        if (oldNickValue != null && newNickValue == null) { // change to the global name from having a nickname
            return "Reset Nickname From: " + MarkdownUtil.underline(oldNickValue.toString());
        }

        if (oldNickValue != null && newNickValue != null) { // changing from one nick to another
            return "Changed Nickname From: " + MarkdownUtil.underline(oldNickValue.toString()) + " to: " + MarkdownUtil.underline(newNickValue.toString());
        }

        // both shouldn't be null which indicates that names couldn't be fetched from the event
        return MarkdownUtil.underline("Changes could not be resolved!");
    }
}
