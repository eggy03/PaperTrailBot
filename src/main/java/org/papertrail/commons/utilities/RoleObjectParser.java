package org.papertrail.commons.utilities;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@UtilityClass
@Slf4j
public class RoleObjectParser {

    // Parses an array list of map of role objects supplied by JDA
    // roles are exposed as arraylists of maps [{name=role, id=1}, {name=role2, id=2}]
	public static String parseRole(Object roleObject) {

        if (roleObject instanceof List<?> roleList) {
            StringBuilder roles = new StringBuilder();
            for (Object o : roleList) {
                if (o instanceof Map<?, ?> roleMap) {
                    roles.append("Name: ").append(roleMap.get("name"))
                            .append(" ID: ").append(roleMap.get("id"))
                            .append(System.lineSeparator());
                }
            }
            return roles.toString();
        }

        return "N/A";
    }
}
