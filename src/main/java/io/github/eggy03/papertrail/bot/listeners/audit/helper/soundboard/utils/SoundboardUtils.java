package io.github.eggy03.papertrail.bot.listeners.audit.helper.soundboard.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;

import static io.github.eggy03.papertrail.bot.commons.utils.NumberFormatUtils.parseDouble;

@UtilityClass
public class SoundboardUtils {

    @NotNull
    public static String resolveVolumePercentage (@Nullable Object decimalVolume) {
        Double volume = parseDouble(decimalVolume, 4, RoundingMode.HALF_UP);
        if(volume==null)
            return "N/A";

        return (volume * 100)+"%";
    }
}
