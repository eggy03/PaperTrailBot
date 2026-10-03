package io.github.eggy03.papertrail.bot.environment;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;
import lombok.NonNull;

@ConfigMapping(prefix = "papertrail")
public interface PaperTrailEnvironment {

    General general();

    EmbedColor embedColor();
    Discord discord();

    API api();

    interface General {

        @WithName("app.name")
        @NonNull
        String appName();

        @WithName("app.version")
        @NonNull
        String appVersion();

        @WithName("github.issue.link")
        @NonNull
        String githubIssueLink();

    }

    interface EmbedColor {

        @WithName("success.color.integer")
        int successColor();

        @WithName("warning.color.integer")
        int warningColor();

        @WithName("destructive.color.integer")
        int destructiveColor();
    }

    interface Discord {

        @WithName("token")
        @NonNull
        String token();
    }

    interface API {

        @WithName("url")
        @NonNull
        String url();
    }

}
