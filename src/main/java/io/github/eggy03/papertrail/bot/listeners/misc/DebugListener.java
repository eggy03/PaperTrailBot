package io.github.eggy03.papertrail.bot.listeners.misc;

import io.github.eggy03.papertrail.bot.commons.constant.ProjectInfo;
import io.github.eggy03.papertrail.bot.commons.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.commons.utils.EnvConfig;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;


@Slf4j
@RequiredArgsConstructor
public class DebugListener extends ListenerAdapter {

    private final ExecutorService vThreadPool;

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
                        .append("\n")
        );

        deniedPermissions.forEach(permission ->
                permString.append("❌ - ")
                        .append(permission.getName())
                        .append("\n")
        );

        return permString.toString().trim();
    }

    @NotNull
    public static String getServerInfo(@NonNull Guild guild, @NonNull GuildChannel channel) {
        return "Server Name: " + MarkdownUtil.underline(guild.getName()) + "\n" +
                "Server ID: " + MarkdownUtil.underline(guild.getId()) + "\n" +
                "Current Channel Name: " + MarkdownUtil.underline(channel.getName()) + "\n" +
                "Current Channel ID: " + MarkdownUtil.underline(channel.getId());
    }

    @NotNull
    public static String getCallerInfo(@NonNull Member member) {
        return "User Name: " + MarkdownUtil.underline(member.getUser().getEffectiveName()) + "\n" +
                "User ID: " + MarkdownUtil.underline(member.getId()) + "\n" +
                "Is Administrator: " + MarkdownUtil.underline(BooleanUtils.formatToYesOrNo(member.hasPermission(Permission.ADMINISTRATOR)));
    }

    @NotNull
    public static String getBotInfo(@NonNull SlashCommandInteractionEvent event) {
        JDA.ShardInfo shardInfo = event.getJDA().getShardInfo();
        return "Current Shard ID: " + shardInfo.getShardId() + "\n" +
                "Total Shards: " + shardInfo.getShardTotal();
    }

    @NotNull
    public static String getConfigurationInfo(@NonNull Guild guild) {
        AuditLogRegistrationClient alClient = new AuditLogRegistrationClient(EnvConfig.get("API_URL"));
        MessageLogRegistrationClient mlClient = new MessageLogRegistrationClient(EnvConfig.get("API_URL"));

        StringBuilder sb = new StringBuilder();

        alClient.getRegisteredGuild(guild.getId()).ifPresentOrElse(entity -> {
            GuildChannel channel = guild.getGuildChannelById(entity.getChannelId());
            if (channel != null)
                sb.append("Registered Audit Log Channel: ").append(MarkdownUtil.underline(channel.getName())).append("\n");
            else
                sb.append("Registered Audit Log Channel: ").append(MarkdownUtil.underline("Registered Channel Unresolvable")).append("\n");
        }, () -> sb.append(MarkdownUtil.underline("No Channel Registered For Audit Logging")).append("\n"));

        mlClient.getRegisteredGuild(guild.getId()).ifPresentOrElse(entity -> {
            GuildChannel channel = guild.getGuildChannelById(entity.getChannelId());
            if (channel != null)
                sb.append("Registered Message Log Channel: ").append(MarkdownUtil.underline(channel.getName()));
            else
                sb.append("Registered Message Log Channel: ").append(MarkdownUtil.underline("Registered Channel Unresolvable"));
        }, () -> sb.append(MarkdownUtil.underline("No Channel Registered For Message Logging")));

        return sb.toString();
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

        vThreadPool.execute(() -> {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Debug Info");
            eb.setDescription("Server: " + guild.getName());
            eb.setColor(Color.GRAY);

            eb.addField(MarkdownUtil.underline("Bot Permissions"), MarkdownUtil.quoteBlock(getBotPermissions(guild)), true);
            eb.addField(MarkdownUtil.underline("Channel Permissions"), MarkdownUtil.quoteBlock(getBotPermissionsInCurrentChannel(guild, channel)), true);
            eb.addField(MarkdownUtil.underline("Server Info"), MarkdownUtil.quoteBlock(getServerInfo(guild, channel)), true);

            eb.addField(MarkdownUtil.underline("User Info"), MarkdownUtil.quoteBlock(getCallerInfo(member)), true);
            eb.addField(MarkdownUtil.underline("Bot Info"), MarkdownUtil.quoteBlock(getBotInfo(event)), true);
            eb.addField(MarkdownUtil.underline("Configuration Info"), MarkdownUtil.quoteBlock(getConfigurationInfo(guild)), true);

            eb.setFooter(ProjectInfo.APPNAME + " " + ProjectInfo.VERSION);
            eb.setTimestamp(Instant.now());

            event.replyEmbeds(eb.build()).queue();
        });
    }
}
