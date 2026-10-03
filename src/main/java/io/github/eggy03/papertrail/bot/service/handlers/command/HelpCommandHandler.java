package io.github.eggy03.papertrail.bot.service.handlers.command;

import io.github.eggy03.papertrail.bot.environment.PaperTrailEnvironment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.Color;
import java.time.Instant;

@ApplicationScoped
public final class HelpCommandHandler {

    private final @NonNull PaperTrailEnvironment paperTrailEnvironment;

    @Inject
    public HelpCommandHandler(@NonNull PaperTrailEnvironment paperTrailEnvironment) {
        this.paperTrailEnvironment = paperTrailEnvironment;
    }

    public void sendInstructions(@NonNull SlashCommandInteractionEvent event) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Setup Guide for " + paperTrailEnvironment.general().appName());
        eb.setDescription("Configure PaperTrailBot to receive different types of server events in separate channels.");
        eb.setColor(Color.decode("#38e8bc"));

        eb.addField("Initial Setup",
                "Use `/setup save` to save your server configuration. You can provide one or more event channels during setup.",
                false
        );

        eb.addField(
                "Guild Events",
                "The **Guild Event Channel** receives events related to the server itself",
                false
        );

        eb.addField(
                "Member Events",
                "The **Member Event Channel** receives events related to server members",
                false
        );

        eb.addField(
                "Message Events",
                "The **Message Event Channel** receives message-related events and logs.",
                false
        );

        eb.addField(
                "View Configuration",
                "Use `/setup view` to **see the channels currently configured** ",
                false
        );

        eb.addField(
                "Update Configuration",
                "Use `/setup update` to **update your event channel configuration**. "
                        + "Any channel option that is not provided will be **cleared** and set to no channel.",
                false
        );

        eb.addField(
                "Remove Configuration",
                "Use `/setup update` without providing any event channels to "
                        + "**remove the existing server configuration**.",
                false
        );

        eb.addField(
                "Permissions",
                "Setup configuration commands require the **Administrator** permission.",
                false
        );

        eb.addBlankField(false);

        eb.addBlankField(false);

        eb.addField("View Server Stats",
                "Use `/stats` to **view useful server information**.",
                false);

        eb.addField("Need help?", "Create an issue on [GitHub](" + paperTrailEnvironment.general().githubIssueLink() + ")", false);
        eb.setFooter(paperTrailEnvironment.general().appName() + " " + paperTrailEnvironment.general().appVersion());
        eb.setTimestamp(Instant.now());

        MessageEmbed mb = eb.build();
        event.replyEmbeds(mb).queue();
    }
}
