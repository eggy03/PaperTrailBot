package io.github.eggy03.papertrail.bot.listeners.auditlog.helper.thread.utils;

import io.github.eggy03.papertrail.bot.commons.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * See {@link io.github.eggy03.papertrail.bot.commons.utils.StackWalkerUtils}
 * and other utilities in the util package
 * to know why logging isn't needed in some of the methods here
 */
@UtilityClass
public class ThreadUtils {

    @NonNull
    public static final String FALLBACK_STRING = "N/A";

    @NotNull
    public static String resolveAutoArchiveDuration(@Nullable Object minutes) {

        Integer minuteInt = NumberParseUtils.parseInt(minutes);

        if (minuteInt == null)
            return FALLBACK_STRING;

        return ThreadChannel.AutoArchiveDuration.fromKey(minuteInt).name();
    }
}
