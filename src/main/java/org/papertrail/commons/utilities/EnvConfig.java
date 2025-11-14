package org.papertrail.commons.utilities;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EnvConfig {

	private static final Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();

	public static String get(String key) {
		
		String value = dotenv.get(key);
		if (value != null) {
			return value;
		}
		
		return System.getenv(key);
	}
}
