package org.papertrail.listeners.commandlisteners;

import java.awt.Color;
import java.util.Objects;

import io.vavr.control.Either;
import org.papertrail.sdk.model.ErrorObject;
import org.papertrail.sdk.client.MessageLogClient;
import org.papertrail.sdk.model.MessageLogObject;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageLogSetupCommandListener extends ListenerAdapter {

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if(!event.getName().equals("messagelog")){
            return;
        }

        if(event.getSubcommandName()==null) {
            return;
        }
			
		switch(event.getSubcommandName()) {
		
		case "set":
			setMessageLogging(event);
			break;
			
		case "view":
			retrieveMessageLoggingChannel(event);
			break;
			
		case "remove":
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

		String guildId = Objects.requireNonNull(event.getGuild()).getId();

        // Call the API to register guild for message logging
        Either<ErrorObject, MessageLogObject> response = MessageLogClient.registerGuild(guildId, event.getChannelId());
        response.peek(success -> {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Message Log Registration");
            eb.addField("✅ Channel Registration Success","╰┈➤"+"All edited and deleted messages will be logged here", false);
            eb.setColor(Color.GREEN);

            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();

        }).peekLeft(failure -> {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Message Log Registration");
            eb.addField("❌ Channel Registration Failure","╰┈➤"+"Channel could not be registered", false);
            eb.addField("\uD83C\uDF10 API Response", "╰┈➤"+failure.message(), false);
            eb.setColor(Color.YELLOW);

            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();
        });

	}
	
	private void retrieveMessageLoggingChannel(SlashCommandInteractionEvent event) {
		
		// Only members with MANAGE_SERVER permissions should be able to use this command
		Member member = event.getMember();
		if (member == null || !member.hasPermission(Permission.ADMINISTRATOR)) {
			event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
			return;
		}
		
		String guildId = Objects.requireNonNull(event.getGuild()).getId();

        // Call the API to check for registered guild
        Either<ErrorObject, MessageLogObject> response = MessageLogClient.getRegisteredGuild(guildId);
        response.peek(success -> {

            String registeredChannelId = success.channelId();
            GuildChannel registeredChannel = event.getJDA().getGuildChannelById(registeredChannelId);

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Message Log Configuration");
            eb.setColor(Color.CYAN);
            eb.addField("✅ Channel Registration Check", "╰┈➤"+(registeredChannel!=null ? registeredChannel.getAsMention() : registeredChannelId)+ " is found to be registered as the message log channel", false);
            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();

        }).peekLeft(failure -> {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Message Log Configuration");
            eb.addField("⚠️ Channel Registration Check", "╰┈➤"+"No channel has been registered for message logs", false);
            eb.setColor(Color.YELLOW);
            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();
        });
	}
	
	private void unsetMessageLogging(SlashCommandInteractionEvent event) {

        // Only members with MANAGE_SERVER permissions should be able to use this command
        Member member = event.getMember();
        if (member == null || !member.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
            return;
        }

        String guildId = Objects.requireNonNull(event.getGuild()).getId();

        // Call the API to unregister guild
        Either<ErrorObject, Void> response = MessageLogClient.deleteRegisteredGuild(guildId);
        response.peek(success -> {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Message Log Configuration");
            eb.addField("✅ Channel Removal", "╰┈➤" + "Channel successfully unset", false);
            eb.setColor(Color.GREEN);

            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();

        }).peekLeft(failure -> {

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Message Log Configuration");
            eb.addField("❌ Channel Removal Failure", "╰┈➤" + "Channel could not be unset", false);
            eb.addField("\uD83C\uDF10 API Response", "╰┈➤" + failure.message(), false);
            eb.setColor(Color.YELLOW);
            MessageEmbed mb = eb.build();

            event.replyEmbeds(mb).setEphemeral(false).queue();

        });

    }
}
