package com.cep.service.configuration;


import com.cep.service.property.RedisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfiguration.class);

    @Autowired
    private RedisProperties redisProperties;

    @Bean(name = "primaryJedisConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory(jedisPoolConfig());
        factory.setHostName(redisProperties.getHost());
        factory.setPort(redisProperties.getPort());
        factory.setTimeout((int) redisProperties.getTimeout());
        factory.setUsePool(true);
        return factory;
    }

    @Bean(name = "primaryJedisPoolConfig")
    public JedisPoolConfig jedisPoolConfig() {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPoolConfig.setMinIdle(redisProperties.getMinIdleConnections());
        jedisPoolConfig.setMaxIdle(redisProperties.getMaxIdleConnections());
        jedisPoolConfig.setMaxTotal(redisProperties.getMaxTotalConnections());

        return jedisPoolConfig;
    }

    @Bean(name = "primaryRedisTemplate")
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

}
