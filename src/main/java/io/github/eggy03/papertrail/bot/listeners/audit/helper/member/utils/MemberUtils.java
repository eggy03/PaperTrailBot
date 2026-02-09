package io.github.eggy03.papertrail.bot.listeners.audit.helper.member.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@UtilityClass
public class MemberUtils {

    // Parses an array list of map of role objects supplied by JDA
    // roles are exposed as arraylists of maps [{name=role, id=1}, {name=role2, id=2}]
    @NotNull
    public static String parseRoleListMap(@NonNull GuildAuditLogEntryCreateEvent event, @Nullable Object roleObject) {

        if(roleObject==null) return "N/A";

        if (roleObject instanceof List<?> roleList) {
            StringBuilder roleString = new StringBuilder();
            roleList.forEach(o -> {
                if (o instanceof Map<?, ?> roleMap && roleMap.containsKey("id")) {
                    String roleId = (String) roleMap.get("id");
                    Role role = event.getGuild().getRoleById(roleId);
                    roleString.append(role!=null ? role.getAsMention() : roleMap.get("name")).append(" ");
                }
            });
            return roleString.toString().trim();
        }
        return "N/A";
    }
}
