package org.papertrail.listeners.audit.event;

import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.papertrail.commons.utilities.EnvConfig;
import org.papertrail.listeners.audit.helper.AutoModerationFlagToChannelEventHelper;
import org.papertrail.listeners.audit.helper.AutoModerationMemberTimeoutEventHelper;
import org.papertrail.listeners.audit.helper.AutoModerationRuleBlockMessageEventHelper;
import org.papertrail.listeners.audit.helper.AutoModerationRuleCreateEventHelper;
import org.papertrail.listeners.audit.helper.AutoModerationRuleDeleteEventHelper;
import org.papertrail.listeners.audit.helper.AutoModerationRuleUpdateEventHelper;
import org.papertrail.listeners.audit.helper.BanEventHelper;
import org.papertrail.listeners.audit.helper.BotAddEventHelper;
import org.papertrail.listeners.audit.helper.ChannelCreateEventHelper;
import org.papertrail.listeners.audit.helper.ChannelDeleteEventHelper;
import org.papertrail.listeners.audit.helper.ChannelOverrideCreateEventHelper;
import org.papertrail.listeners.audit.helper.ChannelOverrideDeleteEventHelper;
import org.papertrail.listeners.audit.helper.ChannelOverrideUpdateEventHelper;
import org.papertrail.listeners.audit.helper.ChannelUpdateEventHelper;
import org.papertrail.listeners.audit.helper.EmojiCreateEventHelper;
import org.papertrail.listeners.audit.helper.EmojiDeleteEventHelper;
import org.papertrail.listeners.audit.helper.EmojiUpdateEventHelper;
import org.papertrail.listeners.audit.helper.GenericAuditLogEventHelper;
import org.papertrail.listeners.audit.helper.GuildUpdateEventHelper;
import org.papertrail.listeners.audit.helper.IntegrationCreateEventHelper;
import org.papertrail.listeners.audit.helper.IntegrationDeleteEventHelper;
import org.papertrail.listeners.audit.helper.InviteCreateEventHelper;
import org.papertrail.listeners.audit.helper.InviteDeleteEventHelper;
import org.papertrail.listeners.audit.helper.KickEventHelper;
import org.papertrail.listeners.audit.helper.MemberRoleUpdateEventHelper;
import org.papertrail.listeners.audit.helper.MemberUpdateEventHelper;
import org.papertrail.listeners.audit.helper.MemberVoiceKickEventHelper;
import org.papertrail.listeners.audit.helper.MemberVoiceMoveEventHelper;
import org.papertrail.listeners.audit.helper.MessagePinEventHelper;
import org.papertrail.listeners.audit.helper.MessageUnpinEventHelper;
import org.papertrail.listeners.audit.helper.OnboardingPromptCreateEventHelper;
import org.papertrail.listeners.audit.helper.OnboardingPromptDeleteEventHelper;
import org.papertrail.listeners.audit.helper.OnboardingPromptUpdateEventHelper;
import org.papertrail.listeners.audit.helper.OnboardingUpdateEventHelper;
import org.papertrail.listeners.audit.helper.RoleCreateEventHelper;
import org.papertrail.listeners.audit.helper.RoleDeleteEventHelper;
import org.papertrail.listeners.audit.helper.RoleUpdateEventHelper;
import org.papertrail.listeners.audit.helper.ScheduledEventCreateEventHelper;
import org.papertrail.listeners.audit.helper.ScheduledEventDeleteEventHelper;
import org.papertrail.listeners.audit.helper.ScheduledEventUpdateEventHelper;
import org.papertrail.listeners.audit.helper.SoundboardSoundCreateEventHelper;
import org.papertrail.listeners.audit.helper.SoundboardSoundDeleteEventHelper;
import org.papertrail.listeners.audit.helper.SoundboardSoundUpdateEventHelper;
import org.papertrail.listeners.audit.helper.StageInstanceCreateEventHelper;
import org.papertrail.listeners.audit.helper.StageInstanceDeleteEventHelper;
import org.papertrail.listeners.audit.helper.StageInstanceUpdateEventHelper;
import org.papertrail.listeners.audit.helper.StickerCreateEventHelper;
import org.papertrail.listeners.audit.helper.StickerDeleteEventHelper;
import org.papertrail.listeners.audit.helper.StickerUpdateEventHelper;
import org.papertrail.listeners.audit.helper.ThreadCreateEventHelper;
import org.papertrail.listeners.audit.helper.ThreadDeleteEventHelper;
import org.papertrail.listeners.audit.helper.ThreadUpdateEventHelper;
import org.papertrail.listeners.audit.helper.UnbanEventHelper;
import org.papertrail.listeners.audit.helper.VoiceChannelStatusDeleteEventHelper;
import org.papertrail.listeners.audit.helper.VoiceChannelStatusUpdateEventHelper;
import org.papertrail.listeners.audit.helper.WebhookCreateEventHelper;
import org.papertrail.listeners.audit.helper.WebhookRemoveEventHelper;
import org.papertrail.listeners.audit.helper.WebhookUpdateEventHelper;

import java.util.Optional;
import java.util.concurrent.Executor;

@Slf4j
public class AuditLogEventListener extends ListenerAdapter {

    private final Executor vThreadPool;
    private static final AuditLogRegistrationClient client = new AuditLogRegistrationClient(EnvConfig.get("API_URL"));

    public AuditLogEventListener(Executor vThreadPool) {
        this.vThreadPool = vThreadPool;
    }

    @Override
    public void onGuildAuditLogEntryCreate(@NotNull GuildAuditLogEntryCreateEvent event) {
        vThreadPool.execute(() -> {
            // Call the API and see if the event came from a registered Guild
            Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());
            response.ifPresent(success -> auditLogParser(event, success.getChannelId()));
        });
    }

    private void auditLogParser(GuildAuditLogEntryCreateEvent event, String channelIdToSendTo) {
        AuditLogEntry ale = event.getEntry();
        ActionType action = ale.getType();
        switch (action) {

            case AUTO_MODERATION_FLAG_TO_CHANNEL -> AutoModerationFlagToChannelEventHelper.format(event, ale, channelIdToSendTo);
            case AUTO_MODERATION_MEMBER_TIMEOUT -> AutoModerationMemberTimeoutEventHelper.format(event, ale, channelIdToSendTo);
            case AUTO_MODERATION_RULE_BLOCK_MESSAGE -> AutoModerationRuleBlockMessageEventHelper.format(event, ale, channelIdToSendTo);
            case AUTO_MODERATION_RULE_CREATE -> AutoModerationRuleCreateEventHelper.format(event, ale, channelIdToSendTo);
            case AUTO_MODERATION_RULE_UPDATE -> AutoModerationRuleUpdateEventHelper.format(event, ale, channelIdToSendTo);
            case AUTO_MODERATION_RULE_DELETE -> AutoModerationRuleDeleteEventHelper.format(event, ale, channelIdToSendTo);

            case KICK -> KickEventHelper.format(event, ale, channelIdToSendTo);
            case BAN -> BanEventHelper.format(event, ale, channelIdToSendTo);
            case UNBAN -> UnbanEventHelper.format(event, ale, channelIdToSendTo);
            case BOT_ADD -> BotAddEventHelper.format(event, ale, channelIdToSendTo);
            // PRUNE has generic formatting

            case CHANNEL_CREATE -> ChannelCreateEventHelper.format(event, ale, channelIdToSendTo);
            case CHANNEL_UPDATE -> ChannelUpdateEventHelper.format(event, ale, channelIdToSendTo);
            case CHANNEL_DELETE -> ChannelDeleteEventHelper.format(event, ale, channelIdToSendTo);
            case CHANNEL_OVERRIDE_CREATE -> ChannelOverrideCreateEventHelper.format(event, ale, channelIdToSendTo);
            case CHANNEL_OVERRIDE_UPDATE -> ChannelOverrideUpdateEventHelper.format(event, ale, channelIdToSendTo);
            case CHANNEL_OVERRIDE_DELETE -> ChannelOverrideDeleteEventHelper.format(event, ale, channelIdToSendTo);

            case ROLE_CREATE -> RoleCreateEventHelper.format(event, ale, channelIdToSendTo);
            case ROLE_UPDATE -> RoleUpdateEventHelper.format(event, ale, channelIdToSendTo);
            case ROLE_DELETE -> RoleDeleteEventHelper.format(event, ale, channelIdToSendTo);

            case EMOJI_CREATE -> EmojiCreateEventHelper.format(event, ale, channelIdToSendTo);
            case EMOJI_UPDATE -> EmojiUpdateEventHelper.format(event, ale, channelIdToSendTo);
            case EMOJI_DELETE -> EmojiDeleteEventHelper.format(event, ale, channelIdToSendTo);

            case STICKER_CREATE -> StickerCreateEventHelper.format(event, ale, channelIdToSendTo);
            case STICKER_UPDATE -> StickerUpdateEventHelper.format(event, ale, channelIdToSendTo);
            case STICKER_DELETE -> StickerDeleteEventHelper.format(event, ale, channelIdToSendTo);

            case GUILD_UPDATE -> GuildUpdateEventHelper.format(event, ale, channelIdToSendTo);

            case INTEGRATION_CREATE -> IntegrationCreateEventHelper.format(event, ale, channelIdToSendTo);
            case INTEGRATION_DELETE -> IntegrationDeleteEventHelper.format(event, ale, channelIdToSendTo);
            // INTEGRATION_UPDATE has generic formatting

            case INVITE_CREATE -> InviteCreateEventHelper.format(event, ale, channelIdToSendTo);
            case INVITE_DELETE -> InviteDeleteEventHelper.format(event, ale, channelIdToSendTo);
            // INVITE_UPDATE has generic formatting

            case MEMBER_ROLE_UPDATE -> MemberRoleUpdateEventHelper.format(event, ale, channelIdToSendTo);
            case MEMBER_UPDATE -> MemberUpdateEventHelper.format(event, ale, channelIdToSendTo);

            case MEMBER_VOICE_KICK -> MemberVoiceKickEventHelper.format(event, ale, channelIdToSendTo);
            case MEMBER_VOICE_MOVE -> MemberVoiceMoveEventHelper.format(event, ale, channelIdToSendTo);

            case MESSAGE_PIN -> MessagePinEventHelper.format(event, ale, channelIdToSendTo);
            case MESSAGE_UNPIN -> MessageUnpinEventHelper.format(event, ale, channelIdToSendTo);

            case SCHEDULED_EVENT_CREATE -> ScheduledEventCreateEventHelper.format(event, ale, channelIdToSendTo);
            case SCHEDULED_EVENT_UPDATE -> ScheduledEventUpdateEventHelper.format(event, ale, channelIdToSendTo);
            case SCHEDULED_EVENT_DELETE -> ScheduledEventDeleteEventHelper.format(event, ale, channelIdToSendTo);

            case STAGE_INSTANCE_CREATE -> StageInstanceCreateEventHelper.format(event, ale, channelIdToSendTo);
            case STAGE_INSTANCE_UPDATE -> StageInstanceUpdateEventHelper.format(event, ale, channelIdToSendTo);
            case STAGE_INSTANCE_DELETE -> StageInstanceDeleteEventHelper.format(event, ale, channelIdToSendTo);

            case THREAD_CREATE -> ThreadCreateEventHelper.format(event, ale, channelIdToSendTo);
            case THREAD_UPDATE -> ThreadUpdateEventHelper.format(event, ale, channelIdToSendTo);
            case THREAD_DELETE -> ThreadDeleteEventHelper.format(event, ale, channelIdToSendTo);

            case VOICE_CHANNEL_STATUS_DELETE -> VoiceChannelStatusDeleteEventHelper.format(event, ale, channelIdToSendTo);
            case VOICE_CHANNEL_STATUS_UPDATE -> VoiceChannelStatusUpdateEventHelper.format(event, ale, channelIdToSendTo);

            case WEBHOOK_CREATE -> WebhookCreateEventHelper.format(event, ale, channelIdToSendTo);
            case WEBHOOK_UPDATE -> WebhookUpdateEventHelper.format(event, ale, channelIdToSendTo);
            case WEBHOOK_REMOVE -> WebhookRemoveEventHelper.format(event, ale, channelIdToSendTo);

            case SOUNDBOARD_SOUND_CREATE -> SoundboardSoundCreateEventHelper.format(event, ale, channelIdToSendTo);
            case SOUNDBOARD_SOUND_UPDATE -> SoundboardSoundUpdateEventHelper.format(event, ale, channelIdToSendTo);
            case SOUNDBOARD_SOUND_DELETE -> SoundboardSoundDeleteEventHelper.format(event, ale, channelIdToSendTo);

            case ONBOARDING_UPDATE -> OnboardingUpdateEventHelper.format(event, channelIdToSendTo);
            case ONBOARDING_PROMPT_CREATE -> OnboardingPromptCreateEventHelper.format(event, channelIdToSendTo);
            case ONBOARDING_PROMPT_UPDATE -> OnboardingPromptUpdateEventHelper.format(event, channelIdToSendTo);
            case ONBOARDING_PROMPT_DELETE -> OnboardingPromptDeleteEventHelper.format(event, channelIdToSendTo);

            case HOME_SETTINGS_CREATE, HOME_SETTINGS_UPDATE -> {
                // might come back later and implement it
                // but for now, don't send these events
            }

            // these seemingly don't trigger properly, or are unreliable
            case MESSAGE_BULK_DELETE,
                 MESSAGE_CREATE,
                 MESSAGE_DELETE,
                 MESSAGE_UPDATE -> GenericAuditLogEventHelper.format(event, ale, channelIdToSendTo);

            // except UNKNOWN, the rest have never been seen to be triggered
            case APPLICATION_COMMAND_PRIVILEGES_UPDATE, PRUNE, INTEGRATION_UPDATE,
                 INVITE_UPDATE, ONBOARDING_CREATE,
                 UNKNOWN -> GenericAuditLogEventHelper.format(event, ale, channelIdToSendTo);

            default -> {
                GenericAuditLogEventHelper.format(event, ale, channelIdToSendTo);
                log.warn("The following event is either not covered by JDA's UNKNOWN type or is not implemented by me yet {}", ale.getType().name());
            }
        }
    }
}
