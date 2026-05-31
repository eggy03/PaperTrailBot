package io.github.eggy03.papertrail.bot.handlers.command;

import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.MessageLogRegistrationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public final class MessageLogSetupCommandHandler {

    private final @NonNull MessageLogRegistrationClient client;

    @Inject
    public MessageLogSetupCommandHandler(@NonNull MessageLogRegistrationClient client) {
        this.client = client;
    }

    public void setMessageLogging(@NonNull SlashCommandInteractionEvent event) {

        Guild callerGuild = event.getGuild();
        GuildChannel callerChannel = event.getChannel().asTextChannel();
        if (callerGuild == null) {
            log.warn("A message log set command may have been called outside of a guild. This should not happen.");
            return;
        }

        // acknowledge this interaction before calling the API
        event.deferReply().queue();

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

        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }

    public void viewMessageLoggingChannel(@NonNull SlashCommandInteractionEvent event) {

        Guild callerGuild = event.getGuild();
        if (callerGuild == null) {
            log.warn("A message log view command may have been called outside of a guild. This should not happen.");
            return;
        }

        // acknowledge this interaction before calling the API
        event.deferReply().queue();

        // Call the API to check for registered guild
        Optional<MessageLogRegistrationEntity> response = client.getRegisteredGuild(callerGuild.getId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("View Existing Message Log Configuration");

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

        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }

    public void unsetMessageLogging(@NonNull SlashCommandInteractionEvent event) {

        Guild callerGuild = event.getGuild();
        if (callerGuild == null) {
            log.warn("A message log unset command may have been called outside of a guild. This should not happen.");
            return;
        }

        // acknowledge this interaction before calling the API
        event.deferReply().queue();

        // Call the API to unregister guild
        boolean success = client.deleteRegisteredGuild(callerGuild.getId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Message Log Removal Process");

        if (success) {
            eb.setColor(Color.GREEN);
            eb.addField(MarkdownUtil.underline("Removal Success"), MarkdownUtil.codeblock("Channel successfully unset"), false);
        } else {
            eb.setColor(Color.YELLOW);
            eb.addField(MarkdownUtil.underline("Removal Failure"), MarkdownUtil.codeblock("Channel could not be unset. This may be because no channel has been registered in this guild yet."), false);
        }

        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }
}
