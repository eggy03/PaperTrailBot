package io.github.eggy03.papertrail.bot.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.NonNull;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.Contract;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.time.Duration;

@ApplicationScoped
public final class RetrofitBeanProducer {

    private final @NonNull String apiUrl;
    private final @NonNull ObjectMapper objectMapper;

    @Inject
    public RetrofitBeanProducer(@NonNull PaperTrailConfig paperTrailConfig, @NonNull ObjectMapper objectMapper) {
        this.apiUrl = paperTrailConfig.api().url();
        this.objectMapper = objectMapper;
    }

    @Contract(" -> new")
    @Produces
    @ApplicationScoped
    public @NonNull Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl(apiUrl)
                .client(new OkHttpClient.Builder().callTimeout(Duration.ofSeconds(60)).readTimeout(Duration.ofSeconds(30)).writeTimeout(Duration.ofSeconds(30)).build())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
    }
}
