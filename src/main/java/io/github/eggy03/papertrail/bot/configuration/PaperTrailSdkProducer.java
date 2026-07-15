package io.github.eggy03.papertrail.bot.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogContentClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;

@ApplicationScoped
public final class PaperTrailSdkProducer {

    private final @NonNull String apiUrl;
    private final @NonNull ObjectMapper objectMapper;

    @Inject
    public PaperTrailSdkProducer(@NonNull PaperTrailConfig paperTrailConfig, @NonNull ObjectMapper objectMapper) {
        this.apiUrl = paperTrailConfig.api().url();
        this.objectMapper = objectMapper;
    }

    @Contract(" -> new")
    @Produces
    @ApplicationScoped
    public @NonNull AuditLogRegistrationClient auditLogRegistrationClient() {
        return new AuditLogRegistrationClient(apiUrl, objectMapper);
    }

    @Contract(" -> new")
    @Produces
    @ApplicationScoped
    public @NonNull MessageLogRegistrationClient messageLogRegistrationClient() {
        return new MessageLogRegistrationClient(apiUrl, objectMapper);
    }

    @Contract(" -> new")
    @Produces
    @ApplicationScoped
    public @NonNull MessageLogContentClient messageLogContentClient() {
        return new MessageLogContentClient(apiUrl, objectMapper);
    }
}
