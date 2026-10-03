package io.github.eggy03.papertrail.bot.service.handlers.command;

import io.github.eggy03.papertrail.bot.environment.PaperTrailEnvironment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

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
        eb.setColor(Color.WHITE);

        eb.addField(
                MarkdownUtil.underline("1. Initial Setup"),
                "Use `/config save [guild_event_channel] [member_event_channel] [message_event_chanel]` to save your server configuration. You can provide one or more event channels during setup.",
                false
        );

        eb.addField(
                MarkdownUtil.quote("Guild Events"),
                MarkdownUtil.quoteBlock("The `guild_event_channel` receives events related to the server itself."),
                false
        );

        eb.addField(
                MarkdownUtil.quote("Member Events"),
                MarkdownUtil.quoteBlock("The `member_event_channel` receives events related to server members."),
                false
        );

        eb.addField(
                MarkdownUtil.quote("Message Events"),
                MarkdownUtil.quoteBlock("The `message_event_channel` receives message-related events and message logs."),
                false
        );

        eb.addField(
                MarkdownUtil.underline("2. View Configuration"),
                "Use `/config view` to see the channels currently configured ",
                false
        );

        eb.addField(
                MarkdownUtil.underline("3. Update Configuration"),
                "Use `/config update [guild_event_channel] [member_event_channel] [message_event_chanel]` to **update** your event channel configuration. "
                        + "Any channel option that is not provided will be **cleared** and set to no channel.",
                false
        );

        eb.addField(
                MarkdownUtil.underline("4. Remove Configuration"),
                "Use `/config update` without providing any event channels to "
                        + "**remove the existing server configuration**.",
                false
        );

        eb.addField(
                MarkdownUtil.quote("Permissions"),
                MarkdownUtil.quoteBlock("Only users with the ADMINISTRATOR permission can use the `/config` command."),
                false
        );

        eb.addBlankField(false);

        eb.addField(
                MarkdownUtil.underline("5. View Server Stats"),
                "Use `/stats` to view useful server information.",
                false
        );

        eb.addField(
                MarkdownUtil.underline("6. View Debug Information"),
                "Use `/debug` to view useful information about the bot. This information may help troubleshoot issues related to the bot.",
                false
        );

        eb.addField("Need help?", "Create an issue on [GitHub](" + paperTrailEnvironment.general().githubIssueLink() + ")", false);
        eb.setFooter(paperTrailEnvironment.general().appName() + " " + paperTrailEnvironment.general().appVersion());
        eb.setTimestamp(Instant.now());

        MessageEmbed mb = eb.build();
        event.replyEmbeds(mb).queue();
    }
}
