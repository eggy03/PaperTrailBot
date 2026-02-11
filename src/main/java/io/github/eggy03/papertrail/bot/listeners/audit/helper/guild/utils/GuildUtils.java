package io.github.eggy03.papertrail.bot.listeners.audit.helper.guild.utils;

import io.github.eggy03.papertrail.bot.commons.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.guild.SystemChannelFlag;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * See {@link io.github.eggy03.papertrail.bot.commons.utils.StackWalkerUtils}
 * and other utilities in the util package
 * to know why logging isn't needed in some of the methods here
 */
@UtilityClass
public class GuildUtils {

    @NonNull
    public static final String FALLBACK_STRING = "N/A";

    // GUILD UTILS
    @NotNull
    public static String resolveGuildVerificationLevel(@Nullable Object verificationLevelInteger) {

        Integer verificationLevel = NumberParseUtils.parseInt(verificationLevelInteger);
        return switch (verificationLevel) { //we could have used JDA's Guild.VerficationLevel but they don't have description
            case 0 -> "NONE";
            case 1 -> "LOW (Verified Email)";
            case 2 -> "MEDIUM (Registered on Discord for more than 5 minutes";
            case 3 -> "HIGH (Must be a member of the server for longer than 10 minutes)";
            case 4 -> "VERY_HIGH (Must have a verified phone number)";
            case null -> FALLBACK_STRING;
            default -> "Unknown";
        };

    }

    @NotNull
    public static String resolveGuildModActionMFALevel(@Nullable Object mfaLevelInteger) {
        Integer mfaLevel = NumberParseUtils.parseInt(mfaLevelInteger);
        if (mfaLevel == null)
            return FALLBACK_STRING;

        return Guild.MFALevel.fromKey(mfaLevel).name();

    }

    @NotNull
    public static String resolveGuildDefaultMessageNotificationLevel(@Nullable Object notificationLevelInteger) {
        Integer notificationLevel = NumberParseUtils.parseInt(notificationLevelInteger);
        if (notificationLevel == null)
            return FALLBACK_STRING;

        return Guild.NotificationLevel.fromKey(notificationLevel).name();

    }

    @NotNull
    public static String resolveExplicitContentFilterLevel(@Nullable Object explicitContentFilterLevelInteger) {
        Integer ecfLevel = NumberParseUtils.parseInt(explicitContentFilterLevelInteger);
        if (ecfLevel == null)
            return FALLBACK_STRING;

        return Guild.ExplicitContentLevel.fromKey(ecfLevel).getDescription();
    }

    @NotNull
    public static String resolveSystemChannelFlags(@Nullable Object systemChannelFlagsIntegerObject) {
        Integer systemChannelFlagsInteger = NumberParseUtils.parseInt(systemChannelFlagsIntegerObject);

        if (systemChannelFlagsInteger == null)
            return FALLBACK_STRING;

        if (systemChannelFlagsInteger == 0)
            return "No Flags Suppressed";

        StringBuilder systemChannelFlags = new StringBuilder();
        SystemChannelFlag
                .getFlags(systemChannelFlagsInteger)
                .forEach(flag ->
                        systemChannelFlags
                                .append(flag.name())
                                .append(System.lineSeparator()));

        return systemChannelFlags.toString().trim();
    }

    // MISC GUILD UTILS (BROADER COVERAGE)
    @NotNull
    public static String resolveMentionableChannel(Object channelId, @NonNull GenericGuildEvent event) {
        Long channelIdLong = NumberParseUtils.parseLong(channelId);
        if (channelIdLong == null)
            return FALLBACK_STRING;

        GuildChannel channel = event.getGuild().getGuildChannelById(channelIdLong);
        return channel != null ? channel.getAsMention() : channelIdLong.toString();
    }
}
