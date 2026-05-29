package io.github.eggy03.papertrail.bot.listeners.auditlog;

import lombok.NonNull;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

/**
 * <p>
 * An abstract class for routing {@link GuildAuditLogEntryCreateEvent}
 * to it's appropriate handlers.
 * The routing is determined by {@link #handleEvent(GuildAuditLogEntryCreateEvent, String)}
 * </p>
 *
 * <p>
 * Subclasses may override only the event handlers they require
 * method determines which handler method to invoke.
 * </p>
 *
 * <p>
 * If multiple subclasses extending this class are registered as CDI beans and are iterated
 * through {@code Instance<AuditLogEntryEventHandler>}, each handler
 * instance will independently receive the event simultaneously.
 * </p>
 *
 * <p>
 * For example, if two subclasses override {@code onBan()}, both overridden
 * methods will execute when a ban event is processed, assuming both handler
 * instances are iterated and invoked.
 * </p>
 *
 * <p>
 * The design philosophy is inspired from JDA's {@link net.dv8tion.jda.api.hooks.ListenerAdapter}
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>{@code
 * @ApplicationScoped
 * public class MyHandler extends AuditLogEntryEventHandler {
 *
 *     @Override
 *     public void onBan(@NonNull GuildAuditLogEntryCreateEvent event,
 *                       @NonNull String channelIdToSendTo) {
 *         // your logic here
 *     }
 * }
 * }</pre>
 *
 * <p>
 * Calling {@code handleEvent(...)} on {@code MyHandler} will automatically
 * route the event to the overridden {@code onBan()} method when the
 * incoming {@link ActionType} is {@code BAN}.
 * </p>
 *
 * <p>
 * Multiple handler beans may also extend this class simultaneously.
 * These handlers can be dynamically resolved and iterated using:
 * </p>
 *
 * <pre>{@code
 * @Inject
 * Instance<AuditLogEntryEventHandler> eventHandlerInstance;
 *
 * eventHandlerInstance.forEach(handler -> handler.handleEvent(...))
 * }</pre>
 *
 * <p>
 * Each handler instance may then independently receive and process the
 * same audit log event through {@code handleEvent(...)}.
 * </p>
 *
 * <p>
 * A concrete implementation of this pattern can be seen in
 * {@link AuditLogEntryEventListener}.
 * </p>
 */
public abstract class AuditLogEntryEventHandler {

    public void onAutoModerationFlagToChannel(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onAutoModerationMemberTimeout(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onAutoModerationRuleBlockMessage(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onAutoModerationRuleCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onAutoModerationRuleUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onAutoModerationRuleDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onKick(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onPrune(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onBan(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onUnban(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onBotAdd(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onChannelCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onChannelUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onChannelDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onChannelOverrideCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onChannelOverrideUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onChannelOverrideDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onRoleCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onRoleUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onRoleDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onEmojiCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onEmojiUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onEmojiDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onStickerCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onStickerUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onStickerDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onGuildUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onGuildProfileUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onIntegrationCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onIntegrationUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onIntegrationDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onApplicationCommandPrivilegesUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onInviteCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onInviteUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onInviteDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onMemberRoleUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onMemberUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onMemberVoiceKick(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onMemberVoiceMove(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onMessagePin(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onMessageUnpin(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onScheduledEventCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onScheduledEventUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onScheduledEventDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onStageInstanceCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onStageInstanceUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onStageInstanceDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onThreadCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onThreadUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onThreadDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onVoiceChannelStatusUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onVoiceChannelStatusDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onWebhookCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onWebhookUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onWebhookDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onSoundboardSoundCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onSoundboardSoundUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onSoundboardSoundDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onOnboardingCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onOnboardingUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onOnboardingPromptCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onOnboardingPromptUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onOnboardingPromptDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onHomeSettingsCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onHomeSettingsUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onMessageBulkDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onMessageCreate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onMessageUpdate(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onMessageDelete(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onUnknownEvent(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    public void onUnimplementedEvent(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
    }

    /**
     * <p>
     * Routes a {@link GuildAuditLogEntryCreateEvent} to its corresponding
     * event handler method based on the {@link ActionType} of the audit log entry.
     * </p>
     *
     * <p>
     * Each event handler method can be overridden multiple times
     * </p>
     *
     * <p>
     * This method is marked as {@code final} and cannot be overridden
     * </p>
     *
     * @param event             the audit log event received from JDA
     * @param channelIdToSendTo the target channel ID where the event should be processed or sent
     */
    public final void handleEvent(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
        ActionType action = event.getEntry().getType();

        switch (action) {
            case AUTO_MODERATION_FLAG_TO_CHANNEL -> onAutoModerationFlagToChannel(event, channelIdToSendTo);
            case AUTO_MODERATION_MEMBER_TIMEOUT -> onAutoModerationMemberTimeout(event, channelIdToSendTo);
            case AUTO_MODERATION_RULE_BLOCK_MESSAGE -> onAutoModerationRuleBlockMessage(event, channelIdToSendTo);
            case AUTO_MODERATION_RULE_CREATE -> onAutoModerationRuleCreate(event, channelIdToSendTo);
            case AUTO_MODERATION_RULE_UPDATE -> onAutoModerationRuleUpdate(event, channelIdToSendTo);
            case AUTO_MODERATION_RULE_DELETE -> onAutoModerationRuleDelete(event, channelIdToSendTo);

            case KICK -> onKick(event, channelIdToSendTo);
            case PRUNE -> onPrune(event, channelIdToSendTo); // never happened to trigger
            case BAN -> onBan(event, channelIdToSendTo);
            case UNBAN -> onUnban(event, channelIdToSendTo);
            case BOT_ADD -> onBotAdd(event, channelIdToSendTo);

            case CHANNEL_CREATE -> onChannelCreate(event, channelIdToSendTo);
            case CHANNEL_UPDATE -> onChannelUpdate(event, channelIdToSendTo);
            case CHANNEL_DELETE -> onChannelDelete(event, channelIdToSendTo);
            case CHANNEL_OVERRIDE_CREATE -> onChannelOverrideCreate(event, channelIdToSendTo);
            case CHANNEL_OVERRIDE_UPDATE -> onChannelOverrideUpdate(event, channelIdToSendTo);
            case CHANNEL_OVERRIDE_DELETE -> onChannelOverrideDelete(event, channelIdToSendTo);

            case ROLE_CREATE -> onRoleCreate(event, channelIdToSendTo);
            case ROLE_UPDATE -> onRoleUpdate(event, channelIdToSendTo);
            case ROLE_DELETE -> onRoleDelete(event, channelIdToSendTo);

            case EMOJI_CREATE -> onEmojiCreate(event, channelIdToSendTo);
            case EMOJI_UPDATE -> onEmojiUpdate(event, channelIdToSendTo);
            case EMOJI_DELETE -> onEmojiDelete(event, channelIdToSendTo);

            case STICKER_CREATE -> onStickerCreate(event, channelIdToSendTo);
            case STICKER_UPDATE -> onStickerUpdate(event, channelIdToSendTo);
            case STICKER_DELETE -> onStickerDelete(event, channelIdToSendTo);

            case GUILD_UPDATE -> onGuildUpdate(event, channelIdToSendTo);
            case GUILD_PROFILE_UPDATE -> onGuildProfileUpdate(event, channelIdToSendTo);

            case INTEGRATION_CREATE -> onIntegrationCreate(event, channelIdToSendTo);
            case INTEGRATION_UPDATE -> onIntegrationUpdate(event, channelIdToSendTo); // never triggers
            case INTEGRATION_DELETE -> onIntegrationDelete(event, channelIdToSendTo);
            case APPLICATION_COMMAND_PRIVILEGES_UPDATE ->
                    onApplicationCommandPrivilegesUpdate(event, channelIdToSendTo);

            case INVITE_CREATE -> onInviteCreate(event, channelIdToSendTo);
            case INVITE_UPDATE -> onInviteUpdate(event, channelIdToSendTo); // never triggers
            case INVITE_DELETE -> onInviteDelete(event, channelIdToSendTo);

            case MEMBER_ROLE_UPDATE -> onMemberRoleUpdate(event, channelIdToSendTo);
            case MEMBER_UPDATE -> onMemberUpdate(event, channelIdToSendTo);

            case MEMBER_VOICE_KICK -> onMemberVoiceKick(event, channelIdToSendTo);
            case MEMBER_VOICE_MOVE -> onMemberVoiceMove(event, channelIdToSendTo);

            case MESSAGE_PIN -> onMessagePin(event, channelIdToSendTo);
            case MESSAGE_UNPIN -> onMessageUnpin(event, channelIdToSendTo);

            // these seemingly don't trigger properly, or are unreliable
            case MESSAGE_BULK_DELETE -> onMessageBulkDelete(event, channelIdToSendTo);
            case MESSAGE_CREATE -> onMessageCreate(event, channelIdToSendTo);
            case MESSAGE_DELETE -> onMessageDelete(event, channelIdToSendTo);
            case MESSAGE_UPDATE -> onMessageUpdate(event, channelIdToSendTo);

            case SCHEDULED_EVENT_CREATE -> onScheduledEventCreate(event, channelIdToSendTo);
            case SCHEDULED_EVENT_UPDATE -> onScheduledEventUpdate(event, channelIdToSendTo);
            case SCHEDULED_EVENT_DELETE -> onScheduledEventDelete(event, channelIdToSendTo);

            case STAGE_INSTANCE_CREATE -> onStageInstanceCreate(event, channelIdToSendTo);
            case STAGE_INSTANCE_UPDATE -> onStageInstanceUpdate(event, channelIdToSendTo);
            case STAGE_INSTANCE_DELETE -> onStageInstanceDelete(event, channelIdToSendTo);

            case THREAD_CREATE -> onThreadCreate(event, channelIdToSendTo);
            case THREAD_UPDATE -> onThreadUpdate(event, channelIdToSendTo);
            case THREAD_DELETE -> onThreadDelete(event, channelIdToSendTo);

            case VOICE_CHANNEL_STATUS_UPDATE -> onVoiceChannelStatusUpdate(event, channelIdToSendTo);
            case VOICE_CHANNEL_STATUS_DELETE -> onVoiceChannelStatusDelete(event, channelIdToSendTo);

            case WEBHOOK_CREATE -> onWebhookCreate(event, channelIdToSendTo);
            case WEBHOOK_UPDATE -> onWebhookUpdate(event, channelIdToSendTo);
            case WEBHOOK_REMOVE -> onWebhookDelete(event, channelIdToSendTo);

            case SOUNDBOARD_SOUND_CREATE -> onSoundboardSoundCreate(event, channelIdToSendTo);
            case SOUNDBOARD_SOUND_UPDATE -> onSoundboardSoundUpdate(event, channelIdToSendTo);
            case SOUNDBOARD_SOUND_DELETE -> onSoundboardSoundDelete(event, channelIdToSendTo);

            case ONBOARDING_CREATE -> onOnboardingCreate(event, channelIdToSendTo); // unreliable
            case ONBOARDING_UPDATE -> onOnboardingUpdate(event, channelIdToSendTo);

            case ONBOARDING_PROMPT_CREATE -> onOnboardingPromptCreate(event, channelIdToSendTo);
            case ONBOARDING_PROMPT_UPDATE -> onOnboardingPromptUpdate(event, channelIdToSendTo);
            case ONBOARDING_PROMPT_DELETE -> onOnboardingPromptDelete(event, channelIdToSendTo);

            case HOME_SETTINGS_CREATE -> onHomeSettingsCreate(event, channelIdToSendTo);
            case HOME_SETTINGS_UPDATE -> onHomeSettingsUpdate(event, channelIdToSendTo);

            case UNKNOWN -> onUnknownEvent(event, channelIdToSendTo);

            default -> onUnimplementedEvent(event, channelIdToSendTo);
        }
    }
}
