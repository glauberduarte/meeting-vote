package com.glauber.voting.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

import java.util.concurrent.TimeUnit;

@Configuration
public class VoteReactiveConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("votedCPFs", "sessions");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .maximumSize(500_000)); // Suporta até 500k registros em memória
        return cacheManager;
    }

    // Fila em memória thread-safe
    @Bean
    public Sinks.Many<VoteEvent> voteSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }

}