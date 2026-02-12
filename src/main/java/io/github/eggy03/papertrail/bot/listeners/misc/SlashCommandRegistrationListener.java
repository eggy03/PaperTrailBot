package io.github.eggy03.papertrail.bot.listeners.misc;

import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/*
 * Registers all the slash commands places throughout the code
 */
public class SlashCommandRegistrationListener extends ListenerAdapter {

    @Override
    public void onReady(@NonNull ReadyEvent event) {
        setAuditLogCommands(event.getJDA());
    }

    private void setAuditLogCommands(@NonNull JDA jda) {

        CommandData auditLog = Commands
                .slash("auditlog", "manage audit log options")
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .addSubcommands(new SubcommandData("set", "set audit log channel here"))
                .addSubcommands(new SubcommandData("view", "view audit log channel"))
                .addSubcommands(new SubcommandData("remove", "unset audit log channel"));

        CommandData messageLog = Commands
                .slash("messagelog", "manage message log options")
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .addSubcommands(new SubcommandData("set", "set message log channel here"))
                .addSubcommands(new SubcommandData("view", "view message log channel"))
                .addSubcommands(new SubcommandData("remove", "unset message log channel"));

        CommandData serverStats = Commands
                .slash("stats", "Provides Server Statistics")
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED);

        CommandData setup = Commands
                .slash("setup", "Provides a guide on setting up the bot")
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED);

        jda.updateCommands()
                .addCommands(auditLog,
                        messageLog,
                        serverStats,
                        setup)
                .queue();
    }
}
