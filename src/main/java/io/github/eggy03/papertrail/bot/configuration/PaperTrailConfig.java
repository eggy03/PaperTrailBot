package io.github.eggy03.papertrail.bot.configuration;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;
import lombok.NonNull;

@ConfigMapping(prefix = "papertrail")
public interface PaperTrailConfig {

    General general();

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

    interface Discord {

        @WithName("token")
        @NonNull
        String token();

        @WithName("twilight.http.proxy.url")
        @NonNull
        String twilightProxyUrl();

        Shard shard();

        interface Shard {

            @WithName("min")
            int min();

            @WithName("max")
            int max();

            @WithName("total")
            int total();
        }
    }

    interface API {

        @WithName("url")
        @NonNull
        String url();
    }

}
