package io.github.eggy03.papertrail.bot.utils.auditlog;

import io.github.eggy03.papertrail.bot.utils.NumberParseUtils;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;

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
