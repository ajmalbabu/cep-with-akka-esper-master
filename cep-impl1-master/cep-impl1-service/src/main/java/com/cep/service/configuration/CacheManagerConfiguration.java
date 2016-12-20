package com.cep.service.configuration;

import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Creates the cache instance for session storage using in memory or redis.
 * </p>
 */
@Configuration
public class CacheManagerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheManagerConfiguration.class);


    @Autowired
    @Qualifier(value = "primaryRedisTemplate")
    private RedisTemplate primaryRedisTemplate;


    @Bean
    @Profile("!isolate")
    public CacheManager sessionCacheManager() {
        LOGGER.info("Using REDIS cache.");
        return new RedisCacheManager(primaryRedisTemplate);
    }

    @Bean
    @Profile("isolate")
    public CacheManager sessionCacheManagerIsolate() {
        LOGGER.info("Using in-memory cache.");
        return inMemoryCacheManager();
    }

    /**
     * <p>
     * In-memory session cache manager. Useful during unit-testing and work station (laptop) development
     * without having REDIS as the session cache manager dependency.
     * </p>
     *
     * @return The CacheManager that will be used.
     */
    private CacheManager inMemoryCacheManager() {

        GuavaCacheManager cacheManager = new GuavaCacheManager();
        cacheManager.setCacheBuilder(CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.HOURS).maximumSize(Long.MAX_VALUE));
        return cacheManager;
    }
}
