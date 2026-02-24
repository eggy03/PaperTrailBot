package io.github.eggy03.papertrail.bot.listeners.misc;

import io.github.eggy03.papertrail.bot.commons.constant.ProjectInfo;
import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;


@Slf4j
public class DebugListener extends ListenerAdapter {

    // recommended permissions for the bot to function
    private static final Set<Permission> recommendedPermissions = EnumSet.of(
            Permission.VIEW_CHANNEL,
            Permission.VIEW_AUDIT_LOGS,
            Permission.MANAGE_SERVER,
            Permission.MESSAGE_SEND,
            Permission.MESSAGE_SEND_IN_THREADS,
            Permission.MESSAGE_EMBED_LINKS,
            Permission.MESSAGE_HISTORY
    );

    @NotNull
    public static String getBotPermissions(@NonNull Guild guild) {
        return formatPermissions(guild.getSelfMember().getPermissions());
    }

    @NotNull
    public static String getBotPermissionsInCurrentChannel(@NonNull Guild guild, @NonNull GuildChannel channel) {
        return formatPermissions(guild.getSelfMember().getPermissions(channel));
    }

    @NotNull
    private static String formatPermissions(@NonNull EnumSet<Permission> grantedGuildOrChannelPermissions) {
        // gets all the permissions granted to the bot in the server as a whole or a particular channel
        EnumSet<Permission> grantedPermissions = EnumSet.copyOf(grantedGuildOrChannelPermissions);
        // this will
        // 1) REMOVE the UNNECESSARY GRANTED PERMISSIONS
        // 2) RETAIN those GRANTED PERMISSIONS that match with the RECOMMENDED ONES
        // there may be cases where granted permissions is NOT a perfect superset of recommended permissions
        // this indicates that some recommended permissions have been DENIED
        grantedPermissions.retainAll(recommendedPermissions);

        // create a copy of recommended permissions
        EnumSet<Permission> deniedPermissions = EnumSet.copyOf(recommendedPermissions);
        // now if you calculate recommended - granted, you will get the set of recommended permissions which are DENIED
        deniedPermissions.removeAll(grantedPermissions);

        StringBuilder permString = new StringBuilder();
        grantedPermissions.forEach(permission ->
                permString.append("✅ - ")
                        .append(permission.getName())
                        .append(System.lineSeparator())
        );

        deniedPermissions.forEach(permission ->
                permString.append("❌ - ")
                        .append(permission.getName())
                        .append(System.lineSeparator())
        );

        return permString.toString().trim();
    }

    @NotNull
    public static String getServerInfo(@NonNull Guild guild, @NonNull GuildChannel channel) {
        return "Server Name: `" + guild.getName() + "`" + System.lineSeparator() +
                "Server ID: `" + guild.getId() + "`" + System.lineSeparator() +
                "Channel Name: `" + channel.getName() + "`" + System.lineSeparator() +
                "Channel ID: `" + channel.getId() + "`";
    }

    @NotNull
    public static String getCallerInfo(@NonNull Member member) {
        return "User Name: `" + member.getUser().getGlobalName() + "`" + System.lineSeparator() +
                "User ID: `" + member.getId() + System.lineSeparator() + "`" +
                "Is Administrator: `" + BooleanUtils.formatToYesOrNo(member.hasPermission(Permission.ADMINISTRATOR)) + "`";
    }

    @NotNull
    public static String getBotInfo(@NonNull SlashCommandInteractionEvent event) {
        JDA.ShardInfo shardInfo = event.getJDA().getShardInfo();
        return "Current Shard ID: `" + shardInfo.getShardId() + System.lineSeparator() + "`" +
                "Total Shards: `" + shardInfo.getShardTotal() + "`";
    }

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("debug"))
            return;

        Guild guild = event.getGuild();
        Member member = event.getMember();
        if (guild == null || member == null) {
            log.warn("command may have been called outside of a guild");
            return;
        }

        GuildChannel channel = event.getGuildChannel();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Debug Info");
        eb.setDescription("Server: " + guild.getName());
        eb.setColor(Color.GRAY);

        eb.addField("Bot Permissions", getBotPermissions(guild), true);
        eb.addField("Channel Permissions", getBotPermissionsInCurrentChannel(guild, channel), true);
        eb.addField("Server Info", getServerInfo(guild, channel), true);

        eb.addField("User Info", getCallerInfo(member), true);
        eb.addBlankField(true);
        eb.addField("Bot Info", getBotInfo(event), true);

        eb.setFooter(ProjectInfo.APPNAME + " " + ProjectInfo.VERSION);
        eb.setTimestamp(Instant.now());

        event.replyEmbeds(eb.build()).queue();
    }
}
