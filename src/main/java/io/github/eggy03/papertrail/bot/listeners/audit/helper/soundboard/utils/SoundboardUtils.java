package io.github.eggy03.papertrail.bot.listeners.audit.helper.soundboard.utils;

import io.github.eggy03.papertrail.bot.commons.utils.NumberParseUtils;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;

/*
 * See {@link io.github.eggy03.papertrail.bot.commons.utils.StackWalkerUtils}
 * and other utilities in the util package
 * to know why logging isn't needed in some of the methods here
 */
@UtilityClass
public class SoundboardUtils {

    @NotNull
    public static String resolveVolumePercentage(@Nullable Object decimalVolume) {
        Double volume = NumberParseUtils.parseDouble(decimalVolume, 4, RoundingMode.HALF_UP);
        if (volume == null)
            return "N/A";

        return (volume * 100) + "%";
    }
}
