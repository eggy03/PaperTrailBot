package io.github.eggy03.papertrail.bot.listeners.auditlog;

import io.github.eggy03.papertrail.bot.service.auditlog.AutoModerationService;
import io.github.eggy03.papertrail.bot.service.auditlog.bot.BotAddEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.channel.ChannelCreateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.channel.ChannelDeleteEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.channel.ChannelOverrideCreateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.channel.ChannelOverrideDeleteEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.channel.ChannelOverrideUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.channel.ChannelUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.channel.VoiceChannelStatusDeleteEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.channel.VoiceChannelStatusUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.emoji.EmojiCreateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.emoji.EmojiDeleteEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.emoji.EmojiUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.generic.GenericAuditLogEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.guild.GuildProfileUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.guild.GuildUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.homesettings.HomeSettingsCreateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.homesettings.HomeSettingsUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.integration.ApplicationCommandPrivilegesUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.integration.IntegrationCreateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.integration.IntegrationDeleteEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.invite.InviteCreateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.invite.InviteDeleteEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.member.MemberRoleUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.member.MemberUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.member.MemberVoiceKickEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.member.MemberVoiceMoveEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.message.MessagePinEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.message.MessageUnpinEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.modactions.BanEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.modactions.KickEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.modactions.UnbanEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.onboarding.OnboardingPromptCreateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.onboarding.OnboardingPromptDeleteEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.onboarding.OnboardingPromptUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.onboarding.OnboardingUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.role.RoleCreateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.role.RoleDeleteEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.role.RoleUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.scheduledevent.ScheduledEventCreateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.scheduledevent.ScheduledEventDeleteEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.scheduledevent.ScheduledEventUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.soundboard.SoundboardSoundCreateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.soundboard.SoundboardSoundDeleteEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.soundboard.SoundboardSoundUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.stage.StageInstanceCreateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.stage.StageInstanceDeleteEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.stage.StageInstanceUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.sticker.StickerCreateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.sticker.StickerDeleteEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.sticker.StickerUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.thread.ThreadCreateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.thread.ThreadDeleteEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.thread.ThreadUpdateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.webhook.WebhookCreateEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.webhook.WebhookRemoveEventHelper;
import io.github.eggy03.papertrail.bot.service.auditlog.webhook.WebhookUpdateEventHelper;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class AuditLogListener extends ListenerAdapter {

    private final @NonNull AuditLogRegistrationClient client;

    private final @NonNull AutoModerationService autoModerationService;


    @Override
    @RunOnVirtualThread
    public void onGuildAuditLogEntryCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
        // Call the API and see if the event came from a registered Guild
        Optional<AuditLogRegistrationEntity> response = client.getRegisteredGuild(event.getGuild().getId());
        response.ifPresent(success -> auditLogParser(event, success.getChannelId()));
    }

    private void auditLogParser(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull String channelIdToSendTo) {
        AuditLogEntry ale = event.getEntry();
        ActionType action = ale.getType();
        switch (action) {

            case AUTO_MODERATION_FLAG_TO_CHANNEL ->
                    autoModerationService.handleFlagToChannelEvent(event, channelIdToSendTo);
            case AUTO_MODERATION_MEMBER_TIMEOUT ->
                    autoModerationService.handleMemberTimeoutEvent(event, channelIdToSendTo);
            case AUTO_MODERATION_RULE_BLOCK_MESSAGE ->
                    autoModerationService.handleRuleBlockMessageEvent(event, channelIdToSendTo);
            case AUTO_MODERATION_RULE_CREATE -> autoModerationService.handleRuleCreateEvent(event, channelIdToSendTo);
            case AUTO_MODERATION_RULE_UPDATE -> autoModerationService.handleRuleUpdateEvent(event, channelIdToSendTo);
            case AUTO_MODERATION_RULE_DELETE -> autoModerationService.handleRuleDeleteEvent(event, channelIdToSendTo);

            case KICK -> KickEventHelper.format(event, channelIdToSendTo);
            case BAN -> BanEventHelper.format(event, channelIdToSendTo);
            case UNBAN -> UnbanEventHelper.format(event, channelIdToSendTo);
            case BOT_ADD -> BotAddEventHelper.format(event, channelIdToSendTo);
            // PRUNE has generic formatting

            case CHANNEL_CREATE -> ChannelCreateEventHelper.format(event, channelIdToSendTo);
            case CHANNEL_UPDATE -> ChannelUpdateEventHelper.format(event, channelIdToSendTo);
            case CHANNEL_DELETE -> ChannelDeleteEventHelper.format(event, channelIdToSendTo);
            case CHANNEL_OVERRIDE_CREATE -> ChannelOverrideCreateEventHelper.format(event, channelIdToSendTo);
            case CHANNEL_OVERRIDE_UPDATE -> ChannelOverrideUpdateEventHelper.format(event, channelIdToSendTo);
            case CHANNEL_OVERRIDE_DELETE -> ChannelOverrideDeleteEventHelper.format(event, channelIdToSendTo);

            case ROLE_CREATE -> RoleCreateEventHelper.format(event, channelIdToSendTo);
            case ROLE_UPDATE -> RoleUpdateEventHelper.format(event, channelIdToSendTo);
            case ROLE_DELETE -> RoleDeleteEventHelper.format(event, channelIdToSendTo);

            case EMOJI_CREATE -> EmojiCreateEventHelper.format(event, channelIdToSendTo);
            case EMOJI_UPDATE -> EmojiUpdateEventHelper.format(event, channelIdToSendTo);
            case EMOJI_DELETE -> EmojiDeleteEventHelper.format(event, channelIdToSendTo);

            case STICKER_CREATE -> StickerCreateEventHelper.format(event, channelIdToSendTo);
            case STICKER_UPDATE -> StickerUpdateEventHelper.format(event, channelIdToSendTo);
            case STICKER_DELETE -> StickerDeleteEventHelper.format(event, channelIdToSendTo);

            case GUILD_UPDATE -> GuildUpdateEventHelper.format(event, channelIdToSendTo);
            case GUILD_PROFILE_UPDATE -> GuildProfileUpdateEventHelper.format(event, channelIdToSendTo);

            case INTEGRATION_CREATE -> IntegrationCreateEventHelper.format(event, channelIdToSendTo);
            case INTEGRATION_DELETE -> IntegrationDeleteEventHelper.format(event, channelIdToSendTo);
            case APPLICATION_COMMAND_PRIVILEGES_UPDATE ->
                    ApplicationCommandPrivilegesUpdateEventHelper.format(event, channelIdToSendTo);
            // INTEGRATION_UPDATE has generic formatting

            case INVITE_CREATE -> InviteCreateEventHelper.format(event, channelIdToSendTo);
            case INVITE_DELETE -> InviteDeleteEventHelper.format(event, channelIdToSendTo);
            // INVITE_UPDATE has generic formatting

            case MEMBER_ROLE_UPDATE -> MemberRoleUpdateEventHelper.format(event, channelIdToSendTo);
            case MEMBER_UPDATE -> MemberUpdateEventHelper.format(event, channelIdToSendTo);

            case MEMBER_VOICE_KICK -> MemberVoiceKickEventHelper.format(event, channelIdToSendTo);
            case MEMBER_VOICE_MOVE -> MemberVoiceMoveEventHelper.format(event, channelIdToSendTo);

            case MESSAGE_PIN -> MessagePinEventHelper.format(event, channelIdToSendTo);
            case MESSAGE_UNPIN -> MessageUnpinEventHelper.format(event, channelIdToSendTo);

            case SCHEDULED_EVENT_CREATE -> ScheduledEventCreateEventHelper.format(event, channelIdToSendTo);
            case SCHEDULED_EVENT_UPDATE -> ScheduledEventUpdateEventHelper.format(event, channelIdToSendTo);
            case SCHEDULED_EVENT_DELETE -> ScheduledEventDeleteEventHelper.format(event, channelIdToSendTo);

            case STAGE_INSTANCE_CREATE -> StageInstanceCreateEventHelper.format(event, channelIdToSendTo);
            case STAGE_INSTANCE_UPDATE -> StageInstanceUpdateEventHelper.format(event, channelIdToSendTo);
            case STAGE_INSTANCE_DELETE -> StageInstanceDeleteEventHelper.format(event, channelIdToSendTo);

            case THREAD_CREATE -> ThreadCreateEventHelper.format(event, channelIdToSendTo);
            case THREAD_UPDATE -> ThreadUpdateEventHelper.format(event, channelIdToSendTo);
            case THREAD_DELETE -> ThreadDeleteEventHelper.format(event, channelIdToSendTo);

            case VOICE_CHANNEL_STATUS_DELETE -> VoiceChannelStatusDeleteEventHelper.format(event, channelIdToSendTo);
            case VOICE_CHANNEL_STATUS_UPDATE -> VoiceChannelStatusUpdateEventHelper.format(event, channelIdToSendTo);

            case WEBHOOK_CREATE -> WebhookCreateEventHelper.format(event, channelIdToSendTo);
            case WEBHOOK_UPDATE -> WebhookUpdateEventHelper.format(event, channelIdToSendTo);
            case WEBHOOK_REMOVE -> WebhookRemoveEventHelper.format(event, channelIdToSendTo);

            case SOUNDBOARD_SOUND_CREATE -> SoundboardSoundCreateEventHelper.format(event, channelIdToSendTo);
            case SOUNDBOARD_SOUND_UPDATE -> SoundboardSoundUpdateEventHelper.format(event, channelIdToSendTo);
            case SOUNDBOARD_SOUND_DELETE -> SoundboardSoundDeleteEventHelper.format(event, channelIdToSendTo);

            case ONBOARDING_UPDATE -> OnboardingUpdateEventHelper.format(event, channelIdToSendTo);
            case ONBOARDING_PROMPT_CREATE -> OnboardingPromptCreateEventHelper.format(event, channelIdToSendTo);
            case ONBOARDING_PROMPT_UPDATE -> OnboardingPromptUpdateEventHelper.format(event, channelIdToSendTo);
            case ONBOARDING_PROMPT_DELETE -> OnboardingPromptDeleteEventHelper.format(event, channelIdToSendTo);

            case HOME_SETTINGS_CREATE -> HomeSettingsCreateEventHelper.format(event, channelIdToSendTo);
            case HOME_SETTINGS_UPDATE -> HomeSettingsUpdateEventHelper.format(event, channelIdToSendTo);

            // these seemingly don't trigger properly, or are unreliable
            case MESSAGE_BULK_DELETE,
                 MESSAGE_CREATE,
                 MESSAGE_DELETE,
                 MESSAGE_UPDATE -> GenericAuditLogEventHelper.format(event, channelIdToSendTo);

            // except UNKNOWN, the rest have never been seen to be triggered
            case PRUNE, INTEGRATION_UPDATE,
                 INVITE_UPDATE, ONBOARDING_CREATE,
                 UNKNOWN -> GenericAuditLogEventHelper.format(event, channelIdToSendTo);

            default -> {
                GenericAuditLogEventHelper.format(event, channelIdToSendTo);
                log.warn("The following event is either not covered by JDA's UNKNOWN type or is not implemented by PaperTrail yet {}", ale.getType().name());
            }
        }
    }
}
