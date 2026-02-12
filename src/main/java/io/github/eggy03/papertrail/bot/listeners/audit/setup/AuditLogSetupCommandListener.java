package io.github.eggy03.papertrail.bot.listeners.audit.setup;

import io.github.eggy03.papertrail.bot.commons.utils.EnvConfig;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.util.Optional;

public class AuditLogSetupCommandListener extends ListenerAdapter {

    @NonNull
    private static final AuditLogRegistrationClient client = new AuditLogRegistrationClient(EnvConfig.get("API_URL"));

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("auditlog")) {
            return;
        }

        if (event.getSubcommandName() == null) {
            return;
        }

        switch (event.getSubcommandName()) {

            case "set":
                setAuditLogging(event);
                break;

            case "view":
                retrieveAuditLoggingChannel(event);
                break;

            case "remove":
                unsetAuditLogging(event);
                break;

            default:
                break;
        }
    }


    private void setAuditLogging(@NonNull SlashCommandInteractionEvent event) {

        // Only members in a guild with MANAGE_SERVER permissions should be able to use this command
        Member callerMember = event.getMember();
        Guild callerGuild = event.getGuild();
        Channel callerChannel = event.getChannel().asTextChannel();

        if (callerMember == null || callerGuild == null) {
            event.reply("❌ You can only use this command in a guild.").setEphemeral(true).queue();
            return;
        }

        if (!callerMember.hasPermission(Permission.MANAGE_SERVER)) {
            event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
            return;
        }

        // Call the API to register the guild and the channel
        boolean success = client.registerGuild(callerGuild.getId(), callerChannel.getId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("📝 Audit Log Configuration");

        if (success) {
            eb.addField("✅ Channel Registration Success", "╰┈➤" + "All audit log info will be logged here", false);
            eb.setColor(Color.GREEN);

            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();
        } else {
            eb.addField("❌ Channel Registration Failure", "╰┈➤" + "Channel could not be registered.\nCheck if a channel in this guild is already registered for logging.", false);
            eb.setColor(Color.YELLOW);

            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();
        }

    }

    private void retrieveAuditLoggingChannel(@NonNull SlashCommandInteractionEvent event) {

        // Only members in a guild with MANAGE_SERVER permissions should be able to use this command
        Member callerMember = event.getMember();
        Guild callerGuild = event.getGuild();

        if (callerMember == null || callerGuild == null) {
            event.reply("❌ You can only use this command in a guild.").setEphemeral(true).queue();
            return;
        }

        if (!callerMember.hasPermission(Permission.MANAGE_SERVER)) {
            event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
            return;
        }


        // Call the API to retrieve the registered channel
        Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(callerGuild.getId());

        // if there is no channel_id for the given guild_id returned by the API, then inform
        // the user of the same, else link the channel that has been registered
        response.ifPresentOrElse(success -> {

            String registeredChannelId = success.getChannelId();
            GuildChannel registeredChannel = event.getJDA().getGuildChannelById(registeredChannelId);

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Audit Log Configuration");
            eb.setColor(Color.CYAN);
            eb.addField("✅ Channel Registration Check", "╰┈➤" + (registeredChannel != null ? registeredChannel.getAsMention() : registeredChannelId) + " is found to be registered as the audit log channel", false);
            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();

        }, () -> {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Audit Log Configuration");
            eb.addField("⚠️ Channel Registration Check", "╰┈➤" + "No channel has been registered for audit logs", false);
            eb.setColor(Color.YELLOW);
            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();
        });
    }

    private void unsetAuditLogging(@NonNull SlashCommandInteractionEvent event) {

        // Only members in a guild with MANAGE_SERVER permissions should be able to use this command
        Member callerMember = event.getMember();
        Guild callerGuild = event.getGuild();

        if (callerMember == null || callerGuild == null) {
            event.reply("❌ You can only use this command in a guild.").setEphemeral(true).queue();
            return;
        }

        if (!callerMember.hasPermission(Permission.MANAGE_SERVER)) {
            event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
            return;
        }

        // Call the API to unregister guild
        boolean success = client.deleteRegisteredGuild(callerGuild.getId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("📝 Audit Log Configuration");

        if (success) {
            eb.addField("✅ Channel Removal", "╰┈➤" + "Channel successfully unset", false);
            eb.setColor(Color.GREEN);

            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();
        } else {
            eb.addField("❌ Channel Removal Failure", "╰┈➤" + "Channel could not be unset.\nThis may be because no channel has been registered in this guild yet.", false);
            eb.setColor(Color.YELLOW);

            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();
        }

    }
}
