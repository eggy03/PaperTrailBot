package io.github.eggy03.papertrail.bot.commons.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class NumberParseUtils {

    @Nullable
    public static Long parseLong(@Nullable Object possibleLongValue) {
        if (possibleLongValue == null) return null;
        try {
            return Long.parseLong(String.valueOf(possibleLongValue));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static Integer parseInt(@Nullable Object possibleIntegerValue) {
        if (possibleIntegerValue == null) return null;
        try {
            return Integer.parseInt(String.valueOf(possibleIntegerValue));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static Double parseDouble (@Nullable Object possibleDoubleValue, int roundingDigits, RoundingMode roundingMode) {
        if(possibleDoubleValue==null) return null;

        try {
            double doubleValue = Double.parseDouble(String.valueOf(possibleDoubleValue));
            return BigDecimal.valueOf(doubleValue).setScale(roundingDigits, roundingMode).doubleValue();
        } catch (NumberFormatException e){
            return null;
        }
    }
}
