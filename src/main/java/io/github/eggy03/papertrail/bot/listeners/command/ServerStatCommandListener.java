package io.github.eggy03.papertrail.bot.listeners.command;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.time.Instant;
import java.util.List;

@Slf4j
public final class ServerStatCommandListener extends ListenerAdapter {

    @NotNull
    private String getMemberAndBotCount(@NonNull Guild guild) {

        List<Member> memberCache = guild.getMemberCache().asList();
        int allUserCount = memberCache.size();
        int botCount = memberCache.stream().filter(member -> member.getUser().isBot()).toList().size();

        return "Users: " + (allUserCount - botCount) + "\nBots: " + botCount + "\nTotal: " + allUserCount;
    }

    @NotNull
    private String getGuildOwner(@NonNull Guild guild) {
        Member owner = guild.getOwner();

        return owner == null ? "N/A" : owner.getAsMention();
    }

    @NotNull
    private String getGuildCreationDate(@NonNull Guild guild) {
        return "<t:" + guild.getTimeCreated().toEpochSecond() + ":f>";
    }

    @NotNull
    private String getGuildVanityUrl(@NonNull Guild guild) {
        return guild.getVanityUrl() == null ? "N/A" : guild.getVanityUrl();
    }

    @NotNull
    private String getBoosters(@NonNull Guild guild) {
        StringBuilder boosterString = new StringBuilder();
        guild.getBoosters().forEach(booster -> boosterString.append(booster.getAsMention()).append(" "));
        return boosterString.toString().trim().isEmpty() ? "No Boosters" : boosterString.toString().trim();
    }

    @NotNull
    private String getBoosterRole(@NonNull Guild guild) {
        return guild.getBoostRole() != null ? guild.getBoostRole().getAsMention() : "No Boost Role Found";
    }

    @NotNull
    private String getDataRequestingMember(@Nullable Member requestingMember) {
        return requestingMember != null ? requestingMember.getEffectiveName() : "N/A";
    }

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {

        if (!event.getName().equals("stats")) {
            return;
        }

        Guild guild = event.getGuild();
        if (guild == null) {
            log.warn("Command may have been called outside of a auditlogsupl");
            return;
        }

        // acknowledge this interaction but reply when the embed's been built
        event.deferReply().queue();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Server Statistics");
        eb.setDescription("Statistics For: " + guild.getName());
        eb.setThumbnail(guild.getIconUrl());
        eb.setColor(Color.PINK);

        eb.addField(MarkdownUtil.underline("Guild Owner"), MarkdownUtil.quoteBlock(getGuildOwner(guild)), false);
        eb.addField(MarkdownUtil.underline("Guild Created On"), MarkdownUtil.quoteBlock(getGuildCreationDate(guild)), false);
        eb.addField(MarkdownUtil.underline("Guild Vanity URL"), MarkdownUtil.quoteBlock(getGuildVanityUrl(guild)), false);
        eb.addField(MarkdownUtil.underline("Member Stats"), MarkdownUtil.quoteBlock(getMemberAndBotCount(guild)), false);
        eb.addField(MarkdownUtil.underline("Guild Boosters"), MarkdownUtil.quoteBlock(getBoosters(guild)), false);
        eb.addField(MarkdownUtil.underline("Guild Boost Count"), MarkdownUtil.quoteBlock(String.valueOf(guild.getBoostCount())), true);
        eb.addField(MarkdownUtil.underline("Booster Role"), MarkdownUtil.quoteBlock(getBoosterRole(guild)), true);
        eb.addField(MarkdownUtil.underline("Boost Tier"), MarkdownUtil.quoteBlock(guild.getBoostTier().name()), true);
        eb.addField(MarkdownUtil.underline("Locale"), MarkdownUtil.quoteBlock(guild.getLocale().getNativeName()), true);
        eb.addField(MarkdownUtil.underline("Verification"), MarkdownUtil.quoteBlock(guild.getVerificationLevel().name()), true);
        eb.addField(MarkdownUtil.underline("Roles"), MarkdownUtil.quoteBlock(String.valueOf(guild.getRoles().size())), true);
        eb.addField(MarkdownUtil.underline("Categories"), MarkdownUtil.quoteBlock(String.valueOf(guild.getCategories().size())), true);
        eb.addField(MarkdownUtil.underline("Text Channels"), MarkdownUtil.quoteBlock(String.valueOf(guild.getTextChannels().size())), true);
        eb.addField(MarkdownUtil.underline("Voice Channels"), MarkdownUtil.quoteBlock(String.valueOf(guild.getVoiceChannels().size())), true);

        eb.setFooter("Requested By: " + getDataRequestingMember(event.getMember()));
        eb.setTimestamp(Instant.now());

        event.getHook().editOriginalEmbeds(eb.build()).queue();

    }
}
