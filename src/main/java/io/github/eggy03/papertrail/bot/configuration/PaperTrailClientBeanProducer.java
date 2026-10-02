package io.github.eggy03.papertrail.bot.configuration;

import io.github.eggy03.papertrail.http.client.PaperTrailGuildClient;
import io.github.eggy03.papertrail.http.client.PaperTrailMessageClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import retrofit2.Retrofit;

@ApplicationScoped
public final class PaperTrailClientBeanProducer {

    private final @NonNull Retrofit retrofit;

    @Inject
    public PaperTrailClientBeanProducer(@NonNull Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    @Contract(" -> new")
    @Produces
    @ApplicationScoped
    public @NonNull PaperTrailGuildClient paperTrailGuildClient() {
        return new PaperTrailGuildClient(retrofit);
    }

    @Contract(" -> new")
    @Produces
    @ApplicationScoped
    public @NonNull PaperTrailMessageClient paperTrailMessageClient() {
        return new PaperTrailMessageClient(retrofit);
    }
}
