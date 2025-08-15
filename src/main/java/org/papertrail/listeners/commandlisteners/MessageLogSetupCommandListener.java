package org.papertrail.listeners.commandlisteners;

import java.awt.Color;
import java.util.Objects;

import org.papertrail.persistencesdk.ApiResponse;
import org.papertrail.persistencesdk.ErrorResponse;
import org.papertrail.persistencesdk.messagelog.MessageLogSetup;
import org.papertrail.persistencesdk.messagelog.MessageLogSetupSuccessResponse;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageLogSetupCommandListener extends ListenerAdapter {

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
			
		switch(event.getName()) {
		
		case "messagelogchannel-set":
			setMessageLogging(event);
			break;
			
		case "messagelogchannel-view":
			retrieveMessageLoggingChannel(event);
			break;
			
		case "messagelogchannel-remove":
			unsetMessageLogging(event);
			break;
			
		default:
			break;
		}
	}

	private void setMessageLogging(SlashCommandInteractionEvent event) {
		
		// Only members with MANAGE_SERVER permissions should be able to use this command
		Member member = event.getMember();
		if (member == null || !member.hasPermission(Permission.ADMINISTRATOR)) {
			event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
			return;
		}
		
		Guild guild = event.getGuild();
		String guildId = Objects.requireNonNull(guild).getId();

        // Call the API to register guild for message logging
        ApiResponse<MessageLogSetupSuccessResponse, ErrorResponse> guildRegistration = MessageLogSetup.registerGuild(guildId, event.getChannelId());
        if(guildRegistration.isSuccess()) {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Message Log Registration");
            eb.addField("✅ Channel Registration Success","╰┈➤"+"All edited and deleted messages will be logged here", false);
            eb.setColor(Color.GREEN);

            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();
        } else {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Message Log Registration");
            eb.addField("❌ Channel Registration Failure","╰┈➤"+"Channel could not be registered", false);
            eb.addField("\uD83C\uDF10 API Response", "╰┈➤"+guildRegistration.error().message(), false);
            eb.setColor(Color.YELLOW);

            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();
        }
	}
	
	private void retrieveMessageLoggingChannel(SlashCommandInteractionEvent event) {
		
		// Only members with MANAGE_SERVER permissions should be able to use this command
		Member member = event.getMember();
		if (member == null || !member.hasPermission(Permission.ADMINISTRATOR)) {
			event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
			return;
		}
		
		Guild guild = event.getGuild();
		String guildId = Objects.requireNonNull(guild).getId();

        // Call the API to check for registered guild
        ApiResponse<MessageLogSetupSuccessResponse, ErrorResponse> guildRegistrationCheck = MessageLogSetup.getRegisteredGuild(guildId);

        if(guildRegistrationCheck.isSuccess()){

            String registeredChannelId = guildRegistrationCheck.success().channelId();
            GuildChannel registeredChannel = event.getJDA().getGuildChannelById(registeredChannelId);

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Message Log Configuration");
            eb.setColor(Color.CYAN);
            eb.addField("✅ Channel Registration Check", "╰┈➤"+(registeredChannel!=null ? registeredChannel.getAsMention() : registeredChannelId)+ " is found to be registered as the message log channel", false);
            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();

        } else {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Message Log Configuration");
            eb.addField("⚠️ Channel Registration Check", "╰┈➤"+"No channel has been registered for message logs", false);
            eb.setColor(Color.YELLOW);
            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();

        }
	}
	
	private void unsetMessageLogging(SlashCommandInteractionEvent event) {

        // Only members with MANAGE_SERVER permissions should be able to use this command
        Member member = event.getMember();
        if (member == null || !member.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
            return;
        }

        Guild guild = event.getGuild();
        String guildId = Objects.requireNonNull(guild).getId();

        // Call the API to unregister guild
        ApiResponse<MessageLogSetupSuccessResponse, ErrorResponse> guildUnregistration = MessageLogSetup.deleteRegisteredGuild(guildId);
        if (guildUnregistration.isSuccess()) {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Message Log Configuration");
            eb.addField("✅ Channel Removal", "╰┈➤" + "Channel successfully unset", false);
            eb.setColor(Color.GREEN);

            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();
        } else {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Message Log Configuration");
            eb.addField("❌ Channel Removal Failure", "╰┈➤" + "Channel could not be unset", false);
            eb.addField("\uD83C\uDF10 API Response", "╰┈➤" + guildUnregistration.error().message(), false);
            eb.setColor(Color.YELLOW);
            MessageEmbed mb = eb.build();

            event.replyEmbeds(mb).setEphemeral(false).queue();

        }
    }
}
