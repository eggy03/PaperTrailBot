package io.github.eggy03.papertrail.bot.listeners.misc;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.time.Instant;
import java.util.List;

@Slf4j
public class ServerStatCommandListener extends ListenerAdapter {

    @NotNull
    public static String getMemberAndBotCount(@NonNull Guild guild) {

        List<Member> memberCache = guild.getMemberCache().asList();
        int allUserCount = memberCache.size();
        int botCount = memberCache.stream().filter(member -> member.getUser().isBot()).toList().size();

        return "Users: " + (allUserCount - botCount) + " Bots: " + botCount + " Total: " + allUserCount;
    }

    @NotNull
    public static String getGuildOwner(@NonNull Guild guild) {
        Member owner = guild.getOwner();

        return owner == null ? "N/A" : owner.getAsMention();
    }

    @NotNull
    public static String getGuildCreationDate(@NonNull Guild guild) {
        return "<t:" + guild.getTimeCreated().toEpochSecond() + ":f>";
    }

    @NotNull
    public static String getGuildVanityUrl(@NonNull Guild guild) {
        return guild.getVanityUrl() == null ? "N/A" : guild.getVanityUrl();
    }

    @NotNull
    public static String getBoosters(@NonNull Guild guild) {
        StringBuilder boosterString = new StringBuilder();
        guild.getBoosters().forEach(booster -> boosterString.append(booster.getAsMention()).append(" "));
        return boosterString.toString().trim().isEmpty() ? "No Boosters" : boosterString.toString().trim();
    }

    @NotNull
    public static String getBoosterRole(@NonNull Guild guild) {
        return guild.getBoostRole() != null ? guild.getBoostRole().getAsMention() : "No Boost Role Found";
    }

    @NotNull
    public static String getDataRequestingMember(@NonNull SlashCommandInteractionEvent event) {
        Member requester = event.getMember();
        return requester != null ? requester.getEffectiveName() : "N/A";
    }

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("stats")) {
            return;
        }

        Guild guild = event.getGuild();
        if (guild == null) {
            log.warn("Command may have been called outside of a guild");
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("📊 Server Statistics 📊");
        eb.setDescription(guild.getName());
        eb.setThumbnail(guild.getIconUrl());
        eb.setColor(Color.PINK);

        eb.addField("Guild Owner", "╰┈➤" + getGuildOwner(guild), false);
        eb.addField("Guild Created On", "╰┈➤" + getGuildCreationDate(guild), false);
        eb.addField("Guild Vanity URL", "╰┈➤" + getGuildVanityUrl(guild), false);

        eb.addField("Member Stats", "╰┈➤" + getMemberAndBotCount(guild), false);

        eb.addField("Guild Boosters ", "╰┈➤" + getBoosters(guild), false);
        eb.addField("Guild Boost Count", "╰┈➤" + guild.getBoostCount(), true);
        eb.addField("Booster Role", "╰┈➤" + getBoosterRole(guild), true);
        eb.addField("Boost Tier", "╰┈➤" + guild.getBoostTier().name(), true);

        eb.addField("Locale", "╰┈➤" + guild.getLocale().getNativeName(), true);
        eb.addField("Verification", "╰┈➤" + guild.getVerificationLevel().name(), true);
        eb.addField("Roles", "╰┈➤" + guild.getRoles().size(), true);
        eb.addField("Categories", "╰┈➤" + guild.getCategories().size(), true);
        eb.addField("Text Channels", "╰┈➤" + guild.getTextChannels().size(), true);
        eb.addField("Voice Channels", "╰┈➤" + guild.getVoiceChannels().size(), true);

        eb.setFooter("Requested By: " + getDataRequestingMember(event));
        eb.setTimestamp(Instant.now());

        MessageEmbed mb = eb.build();
        event.replyEmbeds(mb).setEphemeral(false).queue();

    }
}
