package io.github.eggy03.papertrail.bot.service.handlers.command;

import io.github.eggy03.papertrail.bot.environment.PaperTrailEnvironment;
import io.github.eggy03.papertrail.http.client.PaperTrailGuildClient;
import io.github.eggy03.papertrail.http.entity.PaperTrailGuild;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Optional;

@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class ConfigCommandHandler {

    private final @NonNull PaperTrailGuildClient client;
    private final @NonNull PaperTrailEnvironment environment;

    @Inject
    public ConfigCommandHandler(@NonNull PaperTrailGuildClient client, @NonNull PaperTrailEnvironment environment) {
        this.client = client;
        this.environment = environment;
    }

    public void saveGuild(@NonNull SlashCommandInteractionEvent event) {

        Guild callerGuild = event.getGuild();
        if (callerGuild == null) {
            log.warn("An audit log set command may have been called outside of a guild. This should not happen.");
            return;
        }

        // acknowledge this interaction before calling the API
        event.deferReply().queue();

        OptionMapping guildEventChannelOption = event.getOption("guild_event_channel");
        OptionMapping memberEventChannelOption = event.getOption("member_event_channel");
        OptionMapping messageEventChannelOption = event.getOption("message_event_channel");

        String guildEventChannelId = guildEventChannelOption == null ? null : guildEventChannelOption.getAsChannel().getId();
        String memberEventChannelId = memberEventChannelOption == null ? null : memberEventChannelOption.getAsChannel().getId();
        String messageEventChannelId = messageEventChannelOption == null ? null : messageEventChannelOption.getAsChannel().getId();

        // Call the API to register the guild and the channel
        boolean success = client.saveGuild(callerGuild.getId(), guildEventChannelId, memberEventChannelId, messageEventChannelId);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Bot Setup Process");

        if (success) {
            eb.setColor(environment.embedColor().successColor());
            eb.addField(MarkdownUtil.underline("Setup Success"), MarkdownUtil.codeblock("Events will be logged in your set channels"), false);
        } else {
            eb.setColor(environment.embedColor().warningColor());
            eb.addField(MarkdownUtil.underline("Setup Failure"), MarkdownUtil.codeblock("Channels could not be registered. This could be due to an existing configuration. You must also provide at least one channel during setup."), false);
        }

        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }

    public void viewGuild(@NonNull SlashCommandInteractionEvent event) {

        Guild callerGuild = event.getGuild();
        if (callerGuild == null) {
            log.warn("An audit log view command may have been called outside of a guild. This should not happen.");
            return;
        }

        // acknowledge this interaction before calling the API
        event.deferReply().queue();

        // Call the API to retrieve the registered channel
        Optional<PaperTrailGuild> response = client.getGuild(callerGuild.getId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("View Existing Configuration");

        // if no guild object returned by the API, then inform
        // the user that no channel has been registered, else resolve the channels
        response.ifPresentOrElse(success -> {

            String guildEventChannelId = success.guildEventChannelId();
            String memberEventChannelId = success.memberEventChannelId();
            String messageEventChannelId = success.messageEventChannelId();

            GuildChannel guildEventChannel = guildEventChannelId == null ? null : event.getJDA().getGuildChannelById(guildEventChannelId);
            GuildChannel memberEventChannel = memberEventChannelId == null ? null : event.getJDA().getGuildChannelById(memberEventChannelId);
            GuildChannel messageEventChannel = messageEventChannelId == null ? null : event.getJDA().getGuildChannelById(messageEventChannelId);

            String guildEventChannelJumpUrl = guildEventChannel == null ? "Not Available" : guildEventChannel.getJumpUrl();
            String memberEventChannelJumpUrl = memberEventChannel == null ? "Not Available" : memberEventChannel.getJumpUrl();
            String messageEventChannelJumpUrl = messageEventChannel == null ? "Not Available" : messageEventChannel.getJumpUrl();

            eb.setColor(environment.embedColor().successColor());
            eb.addField(MarkdownUtil.underline("Guild Event Channel"), guildEventChannelJumpUrl, false);
            eb.addField(MarkdownUtil.underline("Member Event Channel"), memberEventChannelJumpUrl, false);
            eb.addField(MarkdownUtil.underline("Message Event Channel"), messageEventChannelJumpUrl, false);
        }, () -> {
            eb.setColor(environment.embedColor().warningColor());
            eb.addField(MarkdownUtil.underline("Warning"), MarkdownUtil.codeblock("No Configuration Found"), false);
        });

        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }

    public void updateGuild(@NonNull SlashCommandInteractionEvent event) {

        Guild callerGuild = event.getGuild();
        if (callerGuild == null) {
            log.warn("An audit log unset command may have been called outside of a guild. This should not happen.");
            return;
        }

        // acknowledge this interaction before calling the API
        event.deferReply().queue();

        OptionMapping guildEventChannelOption = event.getOption("guild_event_channel");
        OptionMapping memberEventChannelOption = event.getOption("member_event_channel");
        OptionMapping messageEventChannelOption = event.getOption("message_event_channel");

        // if all the options are null, unregister the guild
        if (guildEventChannelOption == null && memberEventChannelOption == null && messageEventChannelOption == null) {
            boolean success = client.deleteGuild(callerGuild.getId());

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Delete Existing Configuration");

            if (success) {
                eb.setColor(environment.embedColor().destructiveColor());
                eb.addField(MarkdownUtil.underline("Deletion Success"), MarkdownUtil.codeblock("Configuration Deleted"), false);
            } else {
                eb.setColor(environment.embedColor().warningColor());
                eb.addField(MarkdownUtil.underline("Deletion Failure"), MarkdownUtil.codeblock("No Configuration Found."), false);
            }

            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        // else update guild
        String guildEventChannelId = guildEventChannelOption == null ? null : guildEventChannelOption.getAsChannel().getId();
        String memberEventChannelId = memberEventChannelOption == null ? null : memberEventChannelOption.getAsChannel().getId();
        String messageEventChannelId = messageEventChannelOption == null ? null : messageEventChannelOption.getAsChannel().getId();

        // Call the API to update channel
        boolean success = client.updateGuild(callerGuild.getId(), guildEventChannelId, memberEventChannelId, messageEventChannelId);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Update Existing Configuration");

        if (success) {
            eb.setColor(environment.embedColor().successColor());
            eb.addField(MarkdownUtil.underline("Update Success"), MarkdownUtil.codeblock("Configuration Has Been Updated"), false);
        } else {
            eb.setColor(environment.embedColor().warningColor());
            eb.addField(MarkdownUtil.underline("Update Failure"), MarkdownUtil.codeblock("No Configuration Found."), false);
        }

        event.getHook().editOriginalEmbeds(eb.build()).queue();

    }
}
