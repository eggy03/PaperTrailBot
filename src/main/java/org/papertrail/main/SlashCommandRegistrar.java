package org.papertrail.main;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/*
 * Registers all the slash commands places throughout the code
 */
public class SlashCommandRegistrar extends ListenerAdapter {

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		setAuditLogCommands(event.getJDA());
	}

	private void setAuditLogCommands(JDA jda) {

        CommandData auditLog = Commands.slash("auditlog", "manage audit log options")
                .addSubcommands(new SubcommandData("set", "set audit log channel here"))
                .addSubcommands(new SubcommandData("view", "view audit log channel"))
                .addSubcommands(new SubcommandData("remove", "unset audit log channel"));

        CommandData messageLog = Commands.slash("messagelog", "manage message log options")
                .addSubcommands(new SubcommandData("set", "set message log channel here"))
                .addSubcommands(new SubcommandData("view", "view message log channel"))
                .addSubcommands(new SubcommandData("remove", "unset message log channel"));
		
		CommandData serverStats = Commands.slash("stats",
				"Provides Server Statistics");
		CommandData botInfo = Commands.slash("about",
				"Provides Bot Info");
		CommandData setup = Commands.slash("setup", "Provides a guide on setting up the bot");

		CommandData permCheck = Commands.slash("permcheck", "Checks if the bot has the necessary permissions to operate");
		
		jda.updateCommands()
				.addCommands(auditLog,
                        messageLog,
						serverStats,
						botInfo,
						setup,
						permCheck)			
				.queue();
	}
}
