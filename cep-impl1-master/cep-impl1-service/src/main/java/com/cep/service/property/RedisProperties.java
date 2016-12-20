package com.cep.service.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>
 * This service provides a common location for {@code Redis} related properties.
 * </p>
 */
@Component
public class RedisProperties {

    @Value("${database:1}")
    private String database;

    @Value("${minIdleConnections:1}")
    private int minIdleConnections;

    @Value("${maxIdleConnections:8}")
    private int maxIdleConnections;

    @Value("${maxTotalConnections:8}")
    private int maxTotalConnections;

    @Value("${host:localhost}")
    private String host;

    @Value("${port:6379}")
    private int port;

    @Value("${timeout:10000}")
    private long timeout;


    public RedisProperties() {
    }

    public String getRedisConnection() {
        return "redis://" + getHost() + ":" + getPort();
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMinIdleConnections() {
        return minIdleConnections;
    }

    public void setMinIdleConnections(int minIdleConnections) {
        this.minIdleConnections = minIdleConnections;
    }

    public int getMaxIdleConnections() {
        return maxIdleConnections;
    }

    public void setMaxIdleConnections(int maxIdleConnections) {
        this.maxIdleConnections = maxIdleConnections;
    }

    public int getMaxTotalConnections() {
        return maxTotalConnections;
    }

    public void setMaxTotalConnections(int maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "RedisProperties{" +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", minIdleConnections=" + minIdleConnections +
                ", maxIdleConnections=" + maxIdleConnections +
                ", maxTotalConnections=" + maxTotalConnections +
                ", database='" + database + '\'' +
                ", timeout=" + timeout +
                '}';
    }
}
