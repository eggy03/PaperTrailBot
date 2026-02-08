package io.github.eggy03.papertrail.bot.commons.utilities;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;

import java.util.List;
import java.util.Map;

@UtilityClass
@Slf4j
public class RoleObjectParser {

    // Parses an array list of map of role objects supplied by JDA
    // roles are exposed as arraylists of maps [{name=role, id=1}, {name=role2, id=2}]
	public static String parseRole(GenericGuildEvent event, Object roleObject) {

        if (roleObject instanceof List<?> roleList) {
            StringBuilder roleString = new StringBuilder();
            for (Object o : roleList) {
                if (o instanceof Map<?, ?> roleMap && roleMap.containsKey("id")) {
                    String roleId = (String) roleMap.get("id");
                    Role role = event.getGuild().getRoleById(roleId);
                    roleString.append(role!=null ? role.getAsMention() : roleMap.get("name")).append(" ");
                }
            }
            return roleString.toString();
        }

        return "N/A";
    }
}
