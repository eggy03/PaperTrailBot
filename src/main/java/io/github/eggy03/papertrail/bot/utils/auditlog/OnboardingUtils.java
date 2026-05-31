package io.github.eggy03.papertrail.bot.utils.auditlog;

import io.github.eggy03.papertrail.bot.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public class OnboardingUtils {

    @NonNull
    public static final String FALLBACK_STRING = "N/A";

    @NotNull
    public static String formatMode(@Nullable Object value) {
        if (value == null) {
            log.debug("format mode value was null");
            return FALLBACK_STRING;
        }

        return switch (String.valueOf(value)) {
            case "1" -> "Advanced Mode";
            case "0" -> "Regular Mode";
            default -> {
                log.debug("unrecognized value: {}", value);
                yield "Unrecognized Mode: " + value;
            }
        };
    }

    @NotNull
    public static String resolveChannelsFromList(@NonNull Guild guild, @Nullable Object value) {

        if (!(value instanceof List<?> channelIdList) || channelIdList.isEmpty())
            return FALLBACK_STRING;

        return channelIdList
                .stream()
                .map(NumberParseUtils::parseLong)
                .filter(Objects::nonNull)
                .map(guild::getGuildChannelById)
                .filter(Objects::nonNull)
                .map(GuildChannel::getAsMention)
                .collect(Collectors.joining(" "))
                .trim();

    }
}
