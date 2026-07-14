package io.github.eggy03.papertrail.bot.service.handlers.auditlog;

import io.github.eggy03.papertrail.bot.service.EmbedCheckingService;
import io.github.eggy03.papertrail.bot.utils.auditlog.SoundboardUtils;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;

@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class SoundboardActionTypeHandler extends AbstractGuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull AuditLogRegistrationClient client;
    private final @NonNull EmbedCheckingService embedCheckingService;

    @Inject
    public SoundboardActionTypeHandler(@NonNull AuditLogRegistrationClient client, @NonNull EmbedCheckingService embedCheckingService) {
        this.client = client;
        this.embedCheckingService = embedCheckingService;
    }

    @NonNull
    private String getRegisteredChannelId(@NonNull String guildId) {
        return client.getRegisteredGuild(guildId)
                .map(AuditLogRegistrationEntity::getChannelId).orElse(StringUtils.EMPTY);

    }


    @Override
    public void onSoundboardSoundCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Soundboard Sound Create Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Sound Item Added By: " + mentionableExecutor));
        eb.setColor(Color.GREEN);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "user_id", "sound_id", "id", "guild_id", "available" -> {
                    // skip
                }
                case "volume" ->
                        eb.addField(MarkdownUtil.underline("Volume"), "╰┈➤" + SoundboardUtils.resolveVolumePercentage(newValue), false);

                case "emoji_name" -> eb.addField(MarkdownUtil.underline("Related Emoji"), "╰┈➤" + newValue, false);

                case "emoji_id" -> eb.addField(MarkdownUtil.underline("Related Emoji ID"), "╰┈➤" + newValue, false);

                case "name" -> eb.addField(MarkdownUtil.underline("Sound Item Name"), "╰┈➤" + newValue, false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Soundboard Sound Create: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }

    @Override
    public void onSoundboardSoundUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Soundboard Sound Update Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Sound Item Updated By: " + mentionableExecutor));
        eb.setColor(Color.YELLOW);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "user_id", "sound_id", "id", "guild_id", "available" -> {
                    // skip
                }
                case "volume" -> {
                    eb.addField(MarkdownUtil.underline("Old Volume"), "╰┈➤" + SoundboardUtils.resolveVolumePercentage(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Volume"), "╰┈➤" + SoundboardUtils.resolveVolumePercentage(newValue), true);
                    eb.addBlankField(true);
                }
                case "emoji_name" -> {
                    eb.addField(MarkdownUtil.underline("Old Related Emoji"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Related Emoji"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }
                case "emoji_id" -> {
                    eb.addField(MarkdownUtil.underline("Old Related Emoji ID"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Related Emoji ID"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }
                case "name" -> {
                    eb.addField(MarkdownUtil.underline("Old Sound Item Name"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Sound Item Name"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }
                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Soundboard Sound Update: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }

    @Override
    public void onSoundboardSoundDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Soundboard Sound Delete Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Sound Item Deleted By: " + mentionableExecutor));
        eb.setColor(Color.RED);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "user_id", "sound_id", "id", "guild_id", "available" -> {
                    // skip
                }
                case "volume" ->
                        eb.addField(MarkdownUtil.underline("Volume"), "╰┈➤" + SoundboardUtils.resolveVolumePercentage(oldValue), false);

                case "emoji_name" -> eb.addField(MarkdownUtil.underline("Related Emoji"), "╰┈➤" + oldValue, false);

                case "emoji_id" -> eb.addField(MarkdownUtil.underline("Related Emoji ID"), "╰┈➤" + oldValue, false);

                case "name" -> eb.addField(MarkdownUtil.underline("Sound Item Name"), "╰┈➤" + oldValue, false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Soundboard Sound Delete: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }
}
