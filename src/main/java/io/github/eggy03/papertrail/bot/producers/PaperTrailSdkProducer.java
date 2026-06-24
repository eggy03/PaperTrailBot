package io.github.eggy03.papertrail.bot.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogContentClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.NonNull;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jetbrains.annotations.Contract;

@ApplicationScoped
public final class PaperTrailSdkProducer {

    private final @NonNull String apiUrl;
    private final @NonNull ObjectMapper objectMapper;

    @Inject
    public PaperTrailSdkProducer(@ConfigProperty(name = "api.url") @NonNull String apiUrl, @NonNull ObjectMapper objectMapper) {
        this.apiUrl = apiUrl;
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
