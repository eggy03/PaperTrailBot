package io.github.eggy03.papertrail.bot.listeners.messagelog.setup;

import io.github.eggy03.papertrail.bot.commons.utils.EnvConfig;
import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.MessageLogRegistrationEntity;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;
import java.util.Optional;

@Slf4j
public class MessageLogSetupCommandListener extends ListenerAdapter {

    @NonNull
    private static final MessageLogRegistrationClient client = new MessageLogRegistrationClient(EnvConfig.get("API_URL"));

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("messagelog") || event.getSubcommandName() == null) {
            return;
        }

        switch (event.getSubcommandName()) {
            case "set" -> setMessageLogging(event);
            case "view" -> retrieveMessageLoggingChannel(event);
            case "remove" -> unsetMessageLogging(event);
            default -> {
                // skip
            }
        }
    }

    private void setMessageLogging(@NonNull SlashCommandInteractionEvent event) {

        Guild callerGuild = event.getGuild();
        GuildChannel callerChannel = event.getChannel().asTextChannel();
        if (callerGuild == null) {
            log.warn("A message log set command may have been called outside of a guild. This should not happen.");
            return;
        }

        // Call the API to register guild for message logging
        boolean success = client.registerGuild(callerGuild.getId(), callerChannel.getId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Message Log Registration Process");

        if (success) {
            eb.setColor(Color.GREEN);
            eb.addField(MarkdownUtil.underline("Registration Success"), MarkdownUtil.codeblock("All edited and deleted messages will be logged here"), false);
        } else {
            eb.setColor(Color.YELLOW);
            eb.addField(MarkdownUtil.underline("Registration Failure"), MarkdownUtil.codeblock("Channel could not be registered. Check if a channel in this guild is already registered for logging."), false);

        }

        MessageEmbed mb = eb.build();
        event.replyEmbeds(mb).setEphemeral(false).queue();
    }

    private void retrieveMessageLoggingChannel(@NonNull SlashCommandInteractionEvent event) {

        Guild callerGuild = event.getGuild();
        if (callerGuild == null) {
            log.warn("A message log view command may have been called outside of a guild. This should not happen.");
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("View Existing Message Log Configuration");

        // Call the API to check for registered guild
        Optional<MessageLogRegistrationEntity> response = client.getRegisteredGuild(callerGuild.getId());
        response.ifPresentOrElse(success -> {

            String registeredChannelId = success.getChannelId();
            GuildChannel registeredChannel = event.getJDA().getGuildChannelById(registeredChannelId);
            String registeredChannelName = registeredChannel != null ? registeredChannel.getName() : registeredChannelId;

            eb.setColor(Color.CYAN);
            eb.addField(MarkdownUtil.underline("Success"), MarkdownUtil.codeblock(registeredChannelName + " is found to be registered as the message log channel"), false);
        }, () -> {
            eb.setColor(Color.YELLOW);
            eb.addField(MarkdownUtil.underline("Warning"), MarkdownUtil.codeblock("No channel has been registered for message logs"), false);
        });

        MessageEmbed mb = eb.build();
        event.replyEmbeds(mb).setEphemeral(false).queue();
    }

    private void unsetMessageLogging(@NonNull SlashCommandInteractionEvent event) {

        Guild callerGuild = event.getGuild();
        if (callerGuild == null) {
            log.warn("A message log unset command may have been called outside of a guild. This should not happen.");
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Message Log Un-Registration Process");

        // Call the API to unregister guild
        boolean success = client.deleteRegisteredGuild(callerGuild.getId());
        if (success) {
            eb.setColor(Color.GREEN);
            eb.addField(MarkdownUtil.underline("Un-Registration Success"), MarkdownUtil.codeblock("Channel successfully unset"), false);
        } else {
            eb.setColor(Color.YELLOW);
            eb.addField(MarkdownUtil.underline("Un-Registration Failure"), MarkdownUtil.codeblock("Channel could not be unset. This may be because no channel has been registered in this guild yet."), false);
        }

        MessageEmbed mb = eb.build();
        event.replyEmbeds(mb).setEphemeral(false).queue();
    }
}
