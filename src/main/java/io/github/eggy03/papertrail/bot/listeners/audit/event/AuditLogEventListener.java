package io.github.eggy03.papertrail.bot.listeners.audit.event;

import io.github.eggy03.papertrail.bot.commons.utils.EnvConfig;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.automod.AutoModerationFlagToChannelEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.automod.AutoModerationMemberTimeoutEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.automod.AutoModerationRuleBlockMessageEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.automod.AutoModerationRuleCreateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.automod.AutoModerationRuleDeleteEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.automod.AutoModerationRuleUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.bot.BotAddEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.ChannelCreateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.ChannelDeleteEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.ChannelOverrideCreateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.ChannelOverrideDeleteEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.ChannelOverrideUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.ChannelUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.VoiceChannelStatusDeleteEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.channel.VoiceChannelStatusUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.emoji.EmojiCreateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.emoji.EmojiDeleteEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.emoji.EmojiUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.generic.GenericAuditLogEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.guild.GuildUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.integration.IntegrationCreateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.integration.IntegrationDeleteEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.invite.InviteCreateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.invite.InviteDeleteEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.member.MemberRoleUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.member.MemberUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.member.MemberVoiceKickEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.member.MemberVoiceMoveEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.message.MessagePinEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.message.MessageUnpinEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.modactions.BanEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.modactions.KickEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.modactions.UnbanEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.onboarding.OnboardingPromptCreateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.onboarding.OnboardingPromptDeleteEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.onboarding.OnboardingPromptUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.onboarding.OnboardingUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.role.RoleCreateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.role.RoleDeleteEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.role.RoleUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.scheduledevent.ScheduledEventCreateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.scheduledevent.ScheduledEventDeleteEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.scheduledevent.ScheduledEventUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.soundboard.SoundboardSoundCreateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.soundboard.SoundboardSoundDeleteEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.soundboard.SoundboardSoundUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.stage.StageInstanceCreateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.stage.StageInstanceDeleteEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.stage.StageInstanceUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.sticker.StickerCreateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.sticker.StickerDeleteEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.sticker.StickerUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.thread.ThreadCreateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.thread.ThreadDeleteEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.thread.ThreadUpdateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.webhook.WebhookCreateEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.webhook.WebhookRemoveEventHelper;
import io.github.eggy03.papertrail.bot.listeners.audit.helper.webhook.WebhookUpdateEventHelper;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

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

            case AUTO_MODERATION_FLAG_TO_CHANNEL -> AutoModerationFlagToChannelEventHelper.format(event, channelIdToSendTo);
            case AUTO_MODERATION_MEMBER_TIMEOUT -> AutoModerationMemberTimeoutEventHelper.format(event, channelIdToSendTo);
            case AUTO_MODERATION_RULE_BLOCK_MESSAGE -> AutoModerationRuleBlockMessageEventHelper.format(event, channelIdToSendTo);
            case AUTO_MODERATION_RULE_CREATE -> AutoModerationRuleCreateEventHelper.format(event, channelIdToSendTo);
            case AUTO_MODERATION_RULE_UPDATE -> AutoModerationRuleUpdateEventHelper.format(event, channelIdToSendTo);
            case AUTO_MODERATION_RULE_DELETE -> AutoModerationRuleDeleteEventHelper.format(event, channelIdToSendTo);

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

            case INTEGRATION_CREATE -> IntegrationCreateEventHelper.format(event, channelIdToSendTo);
            case INTEGRATION_DELETE -> IntegrationDeleteEventHelper.format(event, channelIdToSendTo);
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

            case HOME_SETTINGS_CREATE, HOME_SETTINGS_UPDATE -> {
                // might come back later and implement it
                // but for now, don't send these events
            }

            // these seemingly don't trigger properly, or are unreliable
            case MESSAGE_BULK_DELETE,
                 MESSAGE_CREATE,
                 MESSAGE_DELETE,
                 MESSAGE_UPDATE -> GenericAuditLogEventHelper.format(event, channelIdToSendTo);

            // except UNKNOWN, the rest have never been seen to be triggered
            case APPLICATION_COMMAND_PRIVILEGES_UPDATE, PRUNE, INTEGRATION_UPDATE,
                 INVITE_UPDATE, ONBOARDING_CREATE,
                 UNKNOWN -> GenericAuditLogEventHelper.format(event, channelIdToSendTo);

            default -> {
                GenericAuditLogEventHelper.format(event, channelIdToSendTo);
                log.warn("The following event is either not covered by JDA's UNKNOWN type or is not implemented by me yet {}", ale.getType().name());
            }
        }
    }
}
