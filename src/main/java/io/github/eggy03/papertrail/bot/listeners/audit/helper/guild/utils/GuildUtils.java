package io.github.eggy03.papertrail.bot.listeners.audit.helper.guild.utils;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.guild.SystemChannelFlag;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class GuildUtils {

    @NotNull
    public static String resolveGuildVerificationLevel(@Nullable Object verificationLevel) {
        if (verificationLevel == null) {
            return "N/A";
        }

        try {
            int t = Integer.parseInt(verificationLevel.toString());
            return switch (t) { //we could have used JDA's Guild.VerficationLevel but they don't have description
                case 0 -> "NONE";
                case 1 -> "LOW (Verified Email)";
                case 2 -> "MEDIUM (Registered on Discord for more than 5 minutes";
                case 3 -> "HIGH (Must be a member of the server for longer than 10 minutes)";
                case 4 -> "VERY_HIGH (Must have a verified phone number)";
                default -> "Unknown";
            };
        } catch (NumberFormatException e) {
            return "Could not parse verification level";
        }

    }

    @NotNull
    public static String resolveGuildModActionMFALevel(@Nullable Object mfaLevel) {
        if (mfaLevel == null) {
            return "N/A";
        }
        try {
            int t = Integer.parseInt(mfaLevel.toString());
            return Guild.MFALevel.fromKey(t).name();
        } catch (NumberFormatException e) {
            return "Could not parse MFA level";
        }

    }

    @NotNull
    public static String resolveGuildDefaultMessageNotificationLevel(@Nullable Object notificationLevel) {
        if (notificationLevel == null) {
            return "N/A";
        }
        try {
            int t = Integer.parseInt(notificationLevel.toString());
            return Guild.NotificationLevel.fromKey(t).name();
        } catch (NumberFormatException e) {
            return "Could not parse Notification Level";
        }

    }

    @NotNull
    public static String resolveExplicitContentFilterLevel (@Nullable Object explicitContentFilterLevel) {
        if(explicitContentFilterLevel==null)
            return "ECF Level is null";

        try {
            int level = Integer.parseInt(String.valueOf(explicitContentFilterLevel));
            return Guild.ExplicitContentLevel.fromKey(level).getDescription();
        } catch (NumberFormatException e) {
            return "Could not parse ECF Level";
        }
    }

    @NotNull
    public static String resolveSystemChannelFlags (@Nullable Object systemFlagsBitfield) {

        if(systemFlagsBitfield==null)
            return "Bitfield for System Flags was null";

        try {
            StringBuilder systemChannelFlags = new StringBuilder();
            int bitfield = Integer.parseInt(String.valueOf(systemFlagsBitfield));
            if (bitfield==0) return "No Flags Suppressed";

            SystemChannelFlag.getFlags(bitfield).forEach(flag -> systemChannelFlags.append(flag.name()).append(System.lineSeparator()));

            return systemChannelFlags.toString();
        } catch (NumberFormatException e) {
            return "Could not parse System Flags";
        }
    }

    @NotNull
    public static String resolveMentionableChannel (Object channelId, GuildAuditLogEntryCreateEvent event) {
        if(channelId==null)
            return "N/A";

        try {
            long channelIdLong = Long.parseLong(String.valueOf(channelId));
            GuildChannel channel = event.getGuild().getGuildChannelById(channelIdLong);
            return channel!=null ? channel.getAsMention() : String.valueOf(channelIdLong);
        } catch (NumberFormatException e){
            return "Could not parse channel ID";
        }
    }
}
