package io.github.eggy03.papertrail.bot.listeners.auditlog.setup;

import io.github.eggy03.papertrail.bot.commons.utils.EnvConfig;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.util.Optional;

@Slf4j
public class AuditLogSetupCommandListener extends ListenerAdapter {

    @NonNull
    private static final AuditLogRegistrationClient client = new AuditLogRegistrationClient(EnvConfig.get("API_URL"));

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("auditlog") || event.getSubcommandName() == null) {
            return;
        }

        switch (event.getSubcommandName()) {
            case "set" -> setAuditLogging(event);
            case "view" -> retrieveAuditLoggingChannel(event);
            case "remove" -> unsetAuditLogging(event);
            default -> {
                // do nothing
            }
        }
    }

    private void setAuditLogging(@NonNull SlashCommandInteractionEvent event) {

        Guild callerGuild = event.getGuild();
        GuildChannel callerChannel = event.getChannel().asTextChannel();
        if (callerGuild == null) {
            log.warn("An audit log set command may have been called outside of a guild. This should not happen.");
            return;
        }

        // Call the API to register the guild and the channel
        boolean success = client.registerGuild(callerGuild.getId(), callerChannel.getId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Registration Process");

        if (success) {
            eb.setColor(Color.GREEN);
            eb.addField("✅ Channel Registration Success", "╰┈➤" + "All audit log info will be logged here", false);
        } else {
            eb.setColor(Color.YELLOW);
            eb.addField("❌ Channel Registration Failure", "╰┈➤" + "Channel could not be registered.\nCheck if a channel in this guild is already registered for logging.", false);
        }

        MessageEmbed mb = eb.build();
        event.replyEmbeds(mb).setEphemeral(false).queue();
    }

    private void retrieveAuditLoggingChannel(@NonNull SlashCommandInteractionEvent event) {

        Guild callerGuild = event.getGuild();
        if (callerGuild == null) {
            log.warn("An audit log view command may have been called outside of a guild. This should not happen.");
            return;
        }

        // Call the API to retrieve the registered channel
        Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(callerGuild.getId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("View Existing Audit Log Configuration");

        // if there is no channel_id for the given guild_id returned by the API, then inform
        // the user of the same, else link the channel that has been registered
        response.ifPresentOrElse(success -> {
            String registeredChannelId = success.getChannelId();
            GuildChannel registeredChannel = event.getJDA().getGuildChannelById(registeredChannelId);
            String mentionableRegisteredChannel = registeredChannel != null ? registeredChannel.getAsMention() : registeredChannelId;

            eb.setColor(Color.CYAN);
            eb.addField("✅ Channel Registration Check", "╰┈➤" + mentionableRegisteredChannel + " is found to be registered as the audit log channel", false);
        }, () -> {
            eb.setColor(Color.YELLOW);
            eb.addField("⚠️ Channel Registration Check", "╰┈➤" + "No channel has been registered for audit logs", false);
        });

        MessageEmbed mb = eb.build();
        event.replyEmbeds(mb).setEphemeral(false).queue();
    }

    private void unsetAuditLogging(@NonNull SlashCommandInteractionEvent event) {

        Guild callerGuild = event.getGuild();
        if (callerGuild == null) {
            log.warn("An audit log unset command may have been called outside of a guild. This should not happen.");
            return;
        }

        // Call the API to unregister guild
        boolean success = client.deleteRegisteredGuild(callerGuild.getId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Un-Registration Process");

        if (success) {
            eb.setColor(Color.GREEN);
            eb.addField("✅ Channel Removal", "╰┈➤" + "Channel successfully unset", false);
        } else {
            eb.setColor(Color.YELLOW);
            eb.addField("❌ Channel Removal Failure", "╰┈➤" + "Channel could not be unset.\nThis may be because no channel has been registered in this guild yet.", false);
        }

        MessageEmbed mb = eb.build();
        event.replyEmbeds(mb).setEphemeral(false).queue();
    }
}
