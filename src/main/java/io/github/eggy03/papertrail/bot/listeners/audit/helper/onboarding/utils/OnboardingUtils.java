package io.github.eggy03.papertrail.bot.listeners.audit.helper.onboarding.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@UtilityClass
public class OnboardingUtils {

    @NotNull
    public static String formatMode(@Nullable Object value) {
        if(value==null) return "N/A";

        return switch (String.valueOf(value)) {
            case "1" -> "Advanced Mode";
            case "0" -> "Regular Mode";
            default -> "N/A";
        };
    }

    @NotNull
    public static String formatStatus(@Nullable Object value) {
        if(value==null)
            return "N/A";

        if(Boolean.TRUE.equals(value))
            return "Enabled";

        return "Disabled";
    }

    @NotNull
    public static String resolveChannelsFromList(@NonNull Guild guild, @Nullable Object value) {
        if(value == null) return "No Resolvable Channel IDs";

        StringBuilder sb = new StringBuilder();
        if(value instanceof List<?> channelIdList) {
            channelIdList.forEach(channelId -> {
                GuildChannel channel = guild.getGuildChannelById((String) channelId);
                sb.append(channel!=null ? channel.getAsMention() : channelId).append(" ");
            });
        }

        return sb.toString();
    }
}
