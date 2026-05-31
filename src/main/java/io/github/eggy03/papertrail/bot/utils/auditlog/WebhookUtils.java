package io.github.eggy03.papertrail.bot.utils.auditlog;

import io.github.eggy03.papertrail.bot.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
@Slf4j
public class WebhookUtils {

    @NonNull
    public static final String FALLBACK_STRING = "N/A";

    @NotNull
    public static String resolveWebhookEventType(@Nullable Object webhookInteger) {
        Integer webhook = NumberParseUtils.parseInt(webhookInteger);

        return switch (webhook) {
            case 0 -> "Ping";
            case 1 -> "Events";
            case null -> FALLBACK_STRING;
            default -> "Unknown Type: " + webhook;
        };
    }
}
