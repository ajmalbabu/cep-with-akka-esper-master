package com.cep.service.configuration;

public interface CacheService {

    /**
     * To add a cacheProperty into the cache using the key.
     *
     * @param key   - Cache key.
     * @param value - Value to be cached.
     * @return cacheProperty - after putting the value into the preferred cache storage return the value.
     */
    Object add(Object key, Object value);

    /**
     * @param key The key to retrieve the value for from Cache.
     * @return Returns the cacheProperty related to the key.
     */
    <T> T get(Object key);

    /**
     * Remove the cacheProperty for the provided key.
     *
     * @param key The key to remove the value of.
     */
    void remove(Object key);


}
