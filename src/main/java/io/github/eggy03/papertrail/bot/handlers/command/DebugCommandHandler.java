package io.github.eggy03.papertrail.bot.handlers.command;

import io.github.eggy03.papertrail.bot.about.ApplicationInfo;
import io.github.eggy03.papertrail.bot.utils.BooleanUtils;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;

@ApplicationScoped
public final class DebugCommandHandler {

    private final @NonNull AuditLogRegistrationClient auditLogRegistrationClient;
    private final @NonNull MessageLogRegistrationClient messageLogRegistrationClient;
    private final @NonNull ApplicationInfo applicationInfo;

    // necessary permissions for the bot to function
    @NonNull
    private final Set<Permission> necessaryPermissions = EnumSet.of(
            Permission.VIEW_CHANNEL,
            Permission.VIEW_AUDIT_LOGS,
            Permission.MANAGE_SERVER,
            Permission.MESSAGE_SEND,
            Permission.MESSAGE_SEND_IN_THREADS,
            Permission.MESSAGE_EMBED_LINKS,
            Permission.MESSAGE_HISTORY
    );

    @Inject
    public DebugCommandHandler(@NonNull AuditLogRegistrationClient auditLogRegistrationClient, @NonNull MessageLogRegistrationClient messageLogRegistrationClient, @NonNull ApplicationInfo applicationInfo) {
        this.auditLogRegistrationClient = auditLogRegistrationClient;
        this.messageLogRegistrationClient = messageLogRegistrationClient;
        this.applicationInfo = applicationInfo;
    }

    public void sendDebugInfo(@NonNull SlashCommandInteractionEvent event, @NonNull Guild guild, @NonNull Member member) {
        GuildChannel channel = event.getGuildChannel();

        // acknowledge this interaction but reply when the embed's been built
        event.deferReply().queue();

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

        eb.setFooter(applicationInfo.projectName() + " " + applicationInfo.projectVersion());
        eb.setTimestamp(Instant.now());

        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }

    @NonNull
    private String getBotPermissions(@NonNull Guild guild) {
        return formatPermissions(guild.getSelfMember().getPermissions());
    }

    @NonNull
    private String getBotPermissionsInCurrentChannel(@NonNull Guild guild, @NonNull GuildChannel channel) {
        return formatPermissions(guild.getSelfMember().getPermissions(channel));
    }

    @NonNull
    private String formatPermissions(@NonNull EnumSet<Permission> grantedGuildOrChannelPermissions) {
        // gets all the permissions granted to the bot in the server as a whole or a particular channel
        EnumSet<Permission> grantedPermissions = EnumSet.copyOf(grantedGuildOrChannelPermissions);

        // this will
        // 1) REMOVE the UNNECESSARY GRANTED PERMISSIONS (permissions which are granted but not required for bot to function)
        // 2) RETAIN those GRANTED PERMISSIONS that match with the NECESSARY ONES
        // there may be cases where GRANTED PERMISSIONS is NOT a perfect SUPERSET of NECESSARY PERMISSIONS
        // this indicates that some NECESSARY PERMISSIONS have been DENIED
        grantedPermissions.retainAll(necessaryPermissions);

        // create a copy of necessary permissions
        EnumSet<Permission> deniedPermissions = EnumSet.copyOf(necessaryPermissions);
        // now if you calculate necessary - granted, you will get the set of necessary permissions which are DENIED
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

    @NonNull
    private String getServerInfo(@NonNull Guild guild, @NonNull GuildChannel channel) {
        return "Server Name: " + MarkdownUtil.underline(guild.getName()) + "\n" +
                "Server ID: " + MarkdownUtil.underline(guild.getId()) + "\n" +
                "Current Channel Name: " + MarkdownUtil.underline(channel.getName()) + "\n" +
                "Current Channel ID: " + MarkdownUtil.underline(channel.getId());
    }

    @NonNull
    private String getCallerInfo(@NonNull Member member) {
        return "User Name: " + MarkdownUtil.underline(member.getUser().getEffectiveName()) + "\n" +
                "User ID: " + MarkdownUtil.underline(member.getId()) + "\n" +
                "Is Administrator: " + MarkdownUtil.underline(BooleanUtils.formatToYesOrNo(member.hasPermission(Permission.ADMINISTRATOR)));
    }

    @NonNull
    private String getBotInfo(@NonNull SlashCommandInteractionEvent event) {
        JDA.ShardInfo shardInfo = event.getJDA().getShardInfo();
        return "Current Shard ID: " + shardInfo.getShardId() + "\n" +
                "Total Shards: " + shardInfo.getShardTotal();
    }

    @NonNull
    private String getConfigurationInfo(@NonNull Guild guild) {

        StringBuilder sb = new StringBuilder();

        auditLogRegistrationClient.getRegisteredGuild(guild.getId()).ifPresentOrElse(entity -> {
            GuildChannel channel = guild.getGuildChannelById(entity.getChannelId());
            if (channel != null)
                sb.append("Registered Audit Log Channel: ").append(MarkdownUtil.underline(channel.getName())).append("\n");
            else
                sb.append("Registered Audit Log Channel: ").append(MarkdownUtil.underline("Registered Channel Unresolvable")).append("\n");
        }, () -> sb.append(MarkdownUtil.underline("No Channel Registered For Audit Logging")).append("\n"));

        messageLogRegistrationClient.getRegisteredGuild(guild.getId()).ifPresentOrElse(entity -> {
            GuildChannel channel = guild.getGuildChannelById(entity.getChannelId());
            if (channel != null)
                sb.append("Registered Message Log Channel: ").append(MarkdownUtil.underline(channel.getName()));
            else
                sb.append("Registered Message Log Channel: ").append(MarkdownUtil.underline("Registered Channel Unresolvable"));
        }, () -> sb.append(MarkdownUtil.underline("No Channel Registered For Message Logging")));

        return sb.toString();
    }
}
