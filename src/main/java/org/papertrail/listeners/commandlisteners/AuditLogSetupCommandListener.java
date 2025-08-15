package org.papertrail.listeners.commandlisteners;

import java.awt.Color;
import java.util.Objects;

import org.papertrail.persistencesdk.response.ApiResponse;
import org.papertrail.persistencesdk.response.ErrorResponseObject;
import org.papertrail.persistencesdk.call.AuditLogSetupCall;
import org.papertrail.persistencesdk.response.AuditLogSetupSuccessResponseObject;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AuditLogSetupCommandListener extends ListenerAdapter {

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

		switch(event.getName()) {

		case "auditlogchannel-set":
			setAuditLogging(event);
			break;

		case "auditlogchannel-view":
			retrieveAuditLoggingChannel(event);
			break;

		case "auditlogchannel-remove":
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
		String channelIdToRegister = event.getChannel().asTextChannel().getId();
        ApiResponse<AuditLogSetupSuccessResponseObject, ErrorResponseObject> guildRegistration = AuditLogSetupCall.registerGuild(event.getGuild().getId(), channelIdToRegister);
        if(guildRegistration.isSuccess()){

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Audit Log Configuration");
            eb.addField("✅ Channel Registration Success","╰┈➤"+"All audit log info will be logged here", false);
            eb.setColor(Color.GREEN);
            MessageEmbed mb = eb.build();

            event.replyEmbeds(mb).setEphemeral(false).queue();
        } else {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Audit Log Configuration");
            eb.addField("❌ Channel Registration Failure", "╰┈➤" + "Channel could not be registered", false);
            eb.addField("\uD83C\uDF10 API Response", "╰┈➤"+guildRegistration.error().message(), false);
            eb.setColor(Color.YELLOW);
            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();

        }
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
        ApiResponse<AuditLogSetupSuccessResponseObject, ErrorResponseObject> guildRegistrationCheck = AuditLogSetupCall.getRegisteredGuild(guildId);

		// if there is no channel_id for the given guild_id returned by the API, then inform
		// the user of the same, else link the channel that has been registered
		if (guildRegistrationCheck.isError()) {
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("📝 Audit Log Configuration");
			eb.addField("⚠️ Channel Registration Check", "╰┈➤"+"No channel has been registered for audit logs", false);
			eb.setColor(Color.YELLOW);
			MessageEmbed mb = eb.build();
			event.replyEmbeds(mb).setEphemeral(false).queue();
			
		} else {

            String registeredChannelId = guildRegistrationCheck.success().channelId();
			GuildChannel registeredChannel =  event.getJDA().getGuildChannelById(registeredChannelId);

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Audit Log Configuration");
            eb.setColor(Color.CYAN);
            eb.addField("✅ Channel Registration Check", "╰┈➤"+(registeredChannel!=null ? registeredChannel.getAsMention() : registeredChannelId)+ " is found to be registered as the audit log channel", false);
            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();
		}
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
        ApiResponse<AuditLogSetupSuccessResponseObject, ErrorResponseObject> guildUnregistration = AuditLogSetupCall.deleteRegisteredGuild(guildId);
        if(guildUnregistration.isSuccess()) {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Audit Log Configuration");
            eb.addField("✅ Channel Removal", "╰┈➤"+"Channel successfully unset", false);
            eb.setColor(Color.GREEN);
            MessageEmbed mb = eb.build();

            event.replyEmbeds(mb).setEphemeral(false).queue();

        } else {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Audit Log Configuration");
            eb.addField("❌ Channel Removal Failure", "╰┈➤"+"Channel could not be unset", false);
            eb.addField("\uD83C\uDF10 API Response", "╰┈➤"+guildUnregistration.error().message(), false);
            eb.setColor(Color.YELLOW);
            MessageEmbed mb = eb.build();

            event.replyEmbeds(mb).setEphemeral(false).queue();

        }
	}
}
