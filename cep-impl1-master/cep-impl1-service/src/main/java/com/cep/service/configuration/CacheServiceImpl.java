package com.cep.service.configuration;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Implementation of the CacheService.
 * </p>
 */

@Service
public class CacheServiceImpl implements CacheService {

    @CachePut(value = "searchCache", key = "#key")
    public Object add(Object key, Object value) {
        return value;
    }

    @Cacheable(value = "searchCache", key = "#key")
    public Object get(Object key) {
        throw new IllegalStateException("No value found in cache for the key: " + key);
    }

    @CacheEvict(value = "searchCache", key = "#key")
    public void remove(Object key) {
    }
}
