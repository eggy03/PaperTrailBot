package io.github.eggy03.papertrail.bot.producers;

import io.github.eggy03.papertrail.bot.annotations.PlatformThreadFactory;
import io.github.eggy03.papertrail.bot.annotations.VirtualThreadFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.ThreadFactory;

@ApplicationScoped
public final class ThreadFactoryProducer {

    @Produces
    @ApplicationScoped
    @VirtualThreadFactory
    public @NonNull ThreadFactory virtualThreadFactory() {
        return Thread.ofVirtual().name("ptrail-virtual-thread-", 0).factory();
    }

    @Produces
    @ApplicationScoped
    @PlatformThreadFactory
    public @NonNull ThreadFactory platformThreadFactory() {
        return Thread.ofPlatform().name("ptrail-platform-thread-", 0).factory();
    }
}
