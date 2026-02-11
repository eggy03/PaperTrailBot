package io.github.eggy03.papertrail.bot.listeners.audit.helper.member.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@UtilityClass
@Slf4j
public class MemberUtils {

    public static final String FALLBACK_STRING = "N/A";

    // Parses an array list of map of role objects supplied by JDA
    // roles are exposed as arraylists of maps [{name=role, id=1}, {name=role2, id=2}]
    @NotNull
    public static String parseRoleListMap(@NonNull GenericGuildEvent event, @Nullable Object roleObject) {

        if (roleObject == null) {
            log.debug("role object was null");
            return FALLBACK_STRING;
        }

        if (roleObject instanceof List<?> roleList) {
            StringBuilder roleString = new StringBuilder();
            roleList.forEach(o -> {
                if (o instanceof Map<?, ?> roleMap && roleMap.containsKey("id")) {
                    String roleId = (String) roleMap.get("id");
                    Role role = event.getGuild().getRoleById(roleId);
                    roleString.append(role != null ? role.getAsMention() : roleMap.get("name")).append(" ");
                }
            });
            return roleString.toString().trim();
        }

        log.debug("role object was not an instance of a list-map, value: {}", roleObject);
        return FALLBACK_STRING;
    }
}
