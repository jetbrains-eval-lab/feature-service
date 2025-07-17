package com.sivalabs.ft.features.config;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Cache configuration class.
 * Spring Boot auto-configures the cache manager based on the properties in application.properties
 * and the presence of ehcache.xml in the classpath.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cacheManager -> {
            cacheManager.createCache("featureById", cacheConfiguration());
            cacheManager.createCache("featuresByStatus", cacheConfiguration());
        };
    }

    private javax.cache.configuration.Configuration<Object, Object> cacheConfiguration() {
        return Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(
                                Object.class,
                                Object.class,
                                ResourcePoolsBuilder.heap(1000)
                        )
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10)))
                        .build()
        );
    }
}
