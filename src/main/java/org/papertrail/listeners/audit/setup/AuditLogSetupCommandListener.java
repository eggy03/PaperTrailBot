package org.papertrail.listeners.audit.setup;

import io.vavr.control.Either;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.papertrail.commons.sdk.client.AuditLogClient;
import org.papertrail.commons.sdk.model.AuditLogObject;
import org.papertrail.commons.sdk.model.ErrorObject;

import java.awt.Color;
import java.util.Objects;

public class AuditLogSetupCommandListener extends ListenerAdapter {

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if(!event.getName().equals("auditlog")){
            return;
        }

        if(event.getSubcommandName()==null) {
            return;
        }

		switch(event.getSubcommandName()) {

		case "set":
			setAuditLogging(event);
			break;

		case "view":
			retrieveAuditLoggingChannel(event);
			break;

		case "remove":
			unsetAuditLogging(event);
			break;

		default:
			break;
		}
	}


	private void setAuditLogging(SlashCommandInteractionEvent event) {

        // Only members with MANAGE_SERVER permissions should be able to use this command
        Member member = event.getMember();
        if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) {
            event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
            return;
        }

        // Call the API to register the guild and the channel
        String guildId = Objects.requireNonNull(event.getGuild()).getId();
        String channelId = event.getChannel().asTextChannel().getId();

        Either<ErrorObject, AuditLogObject> response = AuditLogClient.registerGuild(guildId, channelId);
        response.peek(success -> {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Audit Log Configuration");
            eb.addField("✅ Channel Registration Success", "╰┈➤" + "All audit log info will be logged here", false);
            eb.setColor(Color.GREEN);
            MessageEmbed mb = eb.build();

            event.replyEmbeds(mb).setEphemeral(false).queue();

        }).peekLeft(failure -> {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Audit Log Configuration");
            eb.addField("❌ Channel Registration Failure", "╰┈➤" + "Channel could not be registered", false);
            eb.addField("\uD83C\uDF10 API Response", "╰┈➤" + failure.message(), false);
            eb.setColor(Color.YELLOW);
            MessageEmbed mb = eb.build();

            event.replyEmbeds(mb).setEphemeral(false).queue();
        });
    }

	private void retrieveAuditLoggingChannel(SlashCommandInteractionEvent event) {
		
		// Only members with MANAGE_SERVER permissions should be able to use this command
		Member member = event.getMember();
		if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) {
			event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
			return;
		}
		
		String guildId = Objects.requireNonNull(event.getGuild()).getId();

		// Call the API to retrieve the registered channel
        Either<ErrorObject, AuditLogObject> response = AuditLogClient.getRegisteredGuild(guildId);

		// if there is no channel_id for the given guild_id returned by the API, then inform
		// the user of the same, else link the channel that has been registered
        response.peek(success -> {

            String registeredChannelId = success.channelId();
            GuildChannel registeredChannel =  event.getJDA().getGuildChannelById(registeredChannelId);

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Audit Log Configuration");
            eb.setColor(Color.CYAN);
            eb.addField("✅ Channel Registration Check", "╰┈➤"+(registeredChannel!=null ? registeredChannel.getAsMention() : registeredChannelId)+ " is found to be registered as the audit log channel", false);
            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();

        }).peekLeft(failure -> {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Audit Log Configuration");
            eb.addField("⚠️ Channel Registration Check", "╰┈➤"+"No channel has been registered for audit logs", false);
            eb.setColor(Color.YELLOW);
            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();

        });
	}

	private void unsetAuditLogging(SlashCommandInteractionEvent event) {
		
		// Only members with MANAGE_SERVER permissions should be able to use this command
		Member member = event.getMember();
		if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) {
			event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
			return;
		}
		
		String guildId = Objects.requireNonNull(event.getGuild()).getId();
        // Call the API to unregister guild
        Either<ErrorObject, Void> response = AuditLogClient.deleteRegisteredGuild(guildId);
        response.peek(success -> {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Audit Log Configuration");
            eb.addField("✅ Channel Removal", "╰┈➤"+"Channel successfully unset", false);
            eb.setColor(Color.GREEN);
            MessageEmbed mb = eb.build();

            event.replyEmbeds(mb).setEphemeral(false).queue();

        }).peekLeft(failure -> {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Audit Log Configuration");
            eb.addField("❌ Channel Removal Failure", "╰┈➤"+"Channel could not be unset", false);
            eb.addField("\uD83C\uDF10 API Response", "╰┈➤"+failure.message(), false);
            eb.setColor(Color.YELLOW);
            MessageEmbed mb = eb.build();

            event.replyEmbeds(mb).setEphemeral(false).queue();

        });

	}
}
