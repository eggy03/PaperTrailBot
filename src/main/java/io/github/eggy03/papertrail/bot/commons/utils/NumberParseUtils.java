package io.github.eggy03.papertrail.bot.commons.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
@Slf4j
public class NumberParseUtils {

    @Nullable
    public static Long parseLong(@Nullable Object possibleLongValue) {

        if (possibleLongValue == null) {
            log.debug("long value is null");
            return null;
        }

        try {
            return Long.parseLong(String.valueOf(possibleLongValue));
        } catch (NumberFormatException e) {
            log.debug("failed to parse long from value={}", possibleLongValue);
            return null;
        }
    }

    @Nullable
    public static Integer parseInt(@Nullable Object possibleIntegerValue) {

        if (possibleIntegerValue == null) {
            log.debug("int value is null");
            return null;
        }
        try {
            return Integer.parseInt(String.valueOf(possibleIntegerValue));
        } catch (NumberFormatException e) {
            log.debug("failed to parse int from value={}", possibleIntegerValue);
            return null;
        }
    }

    @Nullable
    public static Double parseDouble(@Nullable Object possibleDoubleValue, int roundingDigits, @NonNull RoundingMode roundingMode) {

        if (possibleDoubleValue == null) {
            log.debug("double value is null");
            return null;
        }

        try {
            double doubleValue = Double.parseDouble(String.valueOf(possibleDoubleValue));
            return BigDecimal.valueOf(doubleValue).setScale(roundingDigits, roundingMode).doubleValue();
        } catch (NumberFormatException e) {
            log.debug("failed to parse double from value={}, roundingDigits={}, roundingMode={}", possibleDoubleValue, roundingDigits, roundingMode);
            return null;
        }
    }
}
