package io.github.eggy03.papertrail.bot.listeners.message.setup;

import io.github.eggy03.papertrail.bot.commons.utilities.EnvConfig;
import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.MessageLogRegistrationEntity;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.util.Optional;

public class MessageLogSetupCommandListener extends ListenerAdapter {

    private static final MessageLogRegistrationClient client = new MessageLogRegistrationClient(EnvConfig.get("API_URL"));

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

        // Only members in a guild with ADMINISTRATOR permissions should be able to use this command
        Member callerMember = event.getMember();
        Guild callerGuild = event.getGuild();
        TextChannel callerChannel = event.getChannel().asTextChannel();

        if (callerMember == null || callerGuild == null) {
            event.reply("❌ You can only use this command in a guild.").setEphemeral(true).queue();
            return;
        }

        if (!callerMember.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
            return;
        }

        // Call the API to register guild for message logging
        boolean success = client.registerGuild(callerGuild.getId(), callerChannel.getId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Message Log Registration");

        if (success) {
            eb.addField("✅ Channel Registration Success","╰┈➤"+"All edited and deleted messages will be logged here", false);
            eb.setColor(Color.GREEN);

            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();
        } else {
            eb.addField("❌ Channel Registration Failure", "╰┈➤" + "Channel could not be registered.\nCheck if a channel in this guild is already registered for logging.", false);
            eb.setColor(Color.YELLOW);

            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();
        }

    }
	
	private void retrieveMessageLoggingChannel(SlashCommandInteractionEvent event) {

        // Only members in a guild with ADMINISTRATOR permissions should be able to use this command
        Member callerMember = event.getMember();
        Guild callerGuild = event.getGuild();

        if (callerMember == null || callerGuild == null) {
            event.reply("❌ You can only use this command in a guild.").setEphemeral(true).queue();
            return;
        }

        if (!callerMember.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
            return;
        }

        // Call the API to check for registered guild
        Optional<MessageLogRegistrationEntity> response = client.getRegisteredGuild(callerGuild.getId());
        response.ifPresentOrElse(success -> {

            String registeredChannelId = success.getChannelId();
            GuildChannel registeredChannel = event.getJDA().getGuildChannelById(registeredChannelId);

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Message Log Configuration");
            eb.setColor(Color.CYAN);
            eb.addField("✅ Channel Registration Check", "╰┈➤"+(registeredChannel!=null ? registeredChannel.getAsMention() : registeredChannelId)+ " is found to be registered as the message log channel", false);
            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();

        }, () -> {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📝 Message Log Configuration");
            eb.addField("⚠️ Channel Registration Check", "╰┈➤"+"No channel has been registered for message logs", false);
            eb.setColor(Color.YELLOW);
            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();
        });
	}
	
	private void unsetMessageLogging(SlashCommandInteractionEvent event) {

        // Only members in a guild with ADMINISTRATOR permissions should be able to use this command
        Member callerMember = event.getMember();
        Guild callerGuild = event.getGuild();

        if (callerMember == null || callerGuild == null) {
            event.reply("❌ You can only use this command in a guild.").setEphemeral(true).queue();
            return;
        }

        if (!callerMember.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ You don't have the permission required to use this command.").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("📝 Message Log Configuration");


        // Call the API to unregister guild
        boolean success = client.deleteRegisteredGuild(callerGuild.getId());
        if (success) {
            eb.addField("✅ Channel Removal", "╰┈➤" + "Channel successfully unset", false);
            eb.setColor(Color.GREEN);

            MessageEmbed mb = eb.build();
            event.replyEmbeds(mb).setEphemeral(false).queue();
        } else {
            eb.addField("❌ Channel Removal Failure", "╰┈➤"+"Channel could not be unset.\nThis may be because no channel has been registered in this guild yet.", false);
            eb.setColor(Color.YELLOW);
            MessageEmbed mb = eb.build();

            event.replyEmbeds(mb).setEphemeral(false).queue();
        }
    }
}
