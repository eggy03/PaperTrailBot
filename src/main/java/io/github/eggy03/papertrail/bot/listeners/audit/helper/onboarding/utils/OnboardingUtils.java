package io.github.eggy03.papertrail.bot.listeners.audit.helper.onboarding.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        if (value == null) {
            log.debug("channel list was null");
            return FALLBACK_STRING;
        }

        StringBuilder sb = new StringBuilder();
        if (value instanceof List<?> channelIdList) {
            channelIdList.forEach(channelId -> {
                GuildChannel channel = guild.getGuildChannelById((String) channelId);
                sb.append(channel != null ? channel.getAsMention() : channelId).append(" ");
            });
        }

        log.debug("channel list was not an instance of a list, value: {}", value);
        return sb.toString().trim();
    }
}
