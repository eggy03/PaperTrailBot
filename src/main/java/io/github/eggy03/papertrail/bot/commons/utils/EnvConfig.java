package io.github.eggy03.papertrail.bot.commons.utils;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;

@UtilityClass
public class EnvConfig {

    @NonNull
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();

    @Nullable
    public static String get(@NonNull String key) {

        String value = dotenv.get(key);
        if (value != null) {
            return value;
        }

        return System.getenv(key);
    }
}
