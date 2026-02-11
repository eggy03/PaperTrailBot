package io.github.eggy03.papertrail.bot.listeners.misc;

import io.github.eggy03.papertrail.bot.commons.constant.ProjectInfo;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.time.Instant;

public class BotSetupInstructionCommandListener extends ListenerAdapter {
	
	@Override
	public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

		if (event.getName().equals("setup")) {
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("🛠️ Setup Guide for " + ProjectInfo.APPNAME);
			eb.setDescription("Welcome to **" + ProjectInfo.APPNAME + "**!\nHere's how to get started using the bot in your server.");
			eb.setColor(Color.decode("#38e8bc"));

			eb.addField("1️⃣ Register Audit Log Channel",
					"- Use `/auditlog set` to **register the current channel** for receiving audit log updates.\n╰┈➤ User must have `Manage Server` permission.",
					false);

			eb.addField("2️⃣ View Registered Audit Log Channel",
					"- Use `/auditlog view` to **check which channel** is currently registered for audit logs.\nThis is helpful if you're unsure where the audit logs are going.\n╰┈➤ User must have `Manage Server` permission.",
					false);

			eb.addField("3️⃣ Unregister Audit Log Channel",
					"- Use `/auditlog remove` to **unset the audit log channel** if you wish to stop logging or switch to another one.\n╰┈➤ User must have `Manage Server` permission.",
					false);
			
			eb.addBlankField(false);
			
			eb.addField("4️⃣ Register Message Log Channel",
					"- Use `/messagelog set` to **register the current channel** for receiving message logs.\n╰┈➤ User must have the `Administrator` permission.",
					false);

			eb.addField("5️⃣ View Registered Message Log Channel",
					"- Use `/messagelog view` to **check which channel** is currently registered for message logs.\nThis is helpful if you're unsure where the message logs are going.\n╰┈➤ User must have `Administrator` permission.",
					false);

			eb.addField("6️⃣ Unregister Message Log Channel",
					"- Use `/messagelog remove` to **unset the message log channel** if you wish to stop logging or switch to another one.\n╰┈➤ User must have `Administrator` permission.",
					false);
			
			eb.addBlankField(false);

			eb.addField("7️⃣ View Server Stats",
					"- Use `/stats` to **get useful server information** like member count, channel count, and more.",
					false);

			eb.addField("📬 Need help?", "Create an issue on [GitHub](" + ProjectInfo.PROJECT_ISSUE_LINK+")", false);
			eb.setFooter(ProjectInfo.APPNAME+" "+ProjectInfo.VERSION);
			eb.setTimestamp(Instant.now());

			MessageEmbed mb = eb.build();
			event.replyEmbeds(mb).setEphemeral(false).queue();
		}
	}
}

