package com.paxport.storify.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public abstract class TimeoutCache<K,V> {

    private final static Logger logger = LoggerFactory.getLogger(TimeoutCache.class);

    private SimpleCache cache;

    protected String tag() {
        return getClass().getName();
    }

    public void put (String key, V value, long staleMillis) {
        TimeoutCacheEntry<V> entry = new TimeoutCacheEntry(value,staleMillis);
        cache.put(key,entry);
        if(logger.isInfoEnabled()) {
            logger.info("NEW CACHE ENTRY FOR " + tag());
        }
    }

    public Optional<TimeoutCacheEntry<V>> getIfNotStale(String key) {
        Optional<TimeoutCacheEntry> entry = cache.get(key);
        if ( entry.isPresent() ) {
            if ( entry.get().isStale() ) {
                if(logger.isInfoEnabled()) {
                    logger.info("CACHE STALE FOR " + tag() + " cachedAt: " + entry.get().getCachedAt() );
                }
                cache.remove(key);
            }
            else {
                if(logger.isInfoEnabled()) {
                    logger.info("CACHE HIT FOR " + tag() + " cachedAt: " + entry.get().getCachedAt() );
                }
                return Optional.ofNullable(entry.get());
            }
        }
        else {
            if(logger.isInfoEnabled()) {
                logger.info("CACHE MISS FOR " + tag() );
            }
        }
        return Optional.empty();
    }
}
