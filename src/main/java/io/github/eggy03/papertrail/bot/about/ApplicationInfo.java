package io.github.eggy03.papertrail.bot.about;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@SuppressWarnings("java:S6206")
public final class ApplicationInfo {

    private final @NonNull String projectVersion;

    @Inject
    public ApplicationInfo(@ConfigProperty(name = "quarkus.application.version") @NonNull String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public @NonNull String projectName() {
        return "PaperTrail";
    }

    public @NonNull String projectVersion() {
        return "v" + projectVersion;
    }

    public @NonNull String projectIssueLink() {
        return "https://github.com/eggy03/PaperTrailBot/issues";
    }
}
