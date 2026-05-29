package io.github.eggy03.papertrail.bot.bean;

import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogContentClient;
import io.github.eggy03.papertrail.sdk.client.MessageLogRegistrationClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.NonNull;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class PaperTrailSdkBeanProvider {

    private final @NonNull String apiUrl;

    @Inject
    public PaperTrailSdkBeanProvider(@ConfigProperty(name = "api.url") @NonNull String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Produces
    public AuditLogRegistrationClient auditLogRegistrationClient() {
        return new AuditLogRegistrationClient(apiUrl);
    }

    @Produces
    public MessageLogRegistrationClient messageLogRegistrationClient() {
        return new MessageLogRegistrationClient(apiUrl);
    }

    @Produces
    public MessageLogContentClient messageLogContentClient() {
        return new MessageLogContentClient(apiUrl);
    }
}
