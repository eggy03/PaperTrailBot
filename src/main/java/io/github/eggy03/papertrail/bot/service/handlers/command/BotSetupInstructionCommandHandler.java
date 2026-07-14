package io.github.eggy03.papertrail.bot.service.handlers.command;

import io.github.eggy03.papertrail.bot.about.ApplicationInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.Color;
import java.time.Instant;

@ApplicationScoped
public final class BotSetupInstructionCommandHandler {

    private final @NonNull ApplicationInfo applicationInfo;

    @Inject
    public BotSetupInstructionCommandHandler(@NonNull ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public void sendInstructions(@NonNull SlashCommandInteractionEvent event) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("🛠️ Setup Guide for " + applicationInfo.projectName());
        eb.setDescription("Follow the instructions below to set up the bot in your server.");
        eb.setColor(Color.decode("#38e8bc"));

        eb.addField("1️⃣ Set Audit Log Channel",
                "Use `/auditlog set` to **register this channel** for receiving audit log events.",
                false);

        eb.addField("2️⃣ View Audit Log Channel",
                "Use `/auditlog view` to **see which channel** is currently receiving audit logs.",
                false);

        eb.addField("3️⃣ Remove Audit Log Channel",
                "Use `/auditlog remove` to **stop logging** events and remove the registered channel.",
                false);

        eb.addField("🔒 Permissions",
                "These three commands require the **Administrator** permission.",
                false);

        eb.addBlankField(false);

        eb.addField("4️⃣ Set Message Log Channel",
                "Use `/messagelog set` to **register this channel** for receiving message logs.",
                false);

        eb.addField("5️⃣ View Message Log Channel",
                "Use `/messagelog view` to **see which channel** is currently receiving message logs.",
                false);

        eb.addField("6️⃣ Remove Message Log Channel",
                "Use `/messagelog remove` to **stop logging** messages and remove the registered channel.",
                false);

        eb.addField("🔒 Permissions",
                "These three commands require the **Administrator** permission.",
                false);

        eb.addBlankField(false);

        eb.addField("7️⃣ View Server Stats",
                "Use `/stats` to **view useful server information**, including member count, channel count, and more.",
                false);

        eb.addField("📬 Need help?", "Create an issue on [GitHub](" + applicationInfo.projectIssueLink() + ")", false);
        eb.setFooter(applicationInfo.projectName() + " " + applicationInfo.projectVersion());
        eb.setTimestamp(Instant.now());

        MessageEmbed mb = eb.build();
        event.replyEmbeds(mb).queue();
    }
}
