package com.paxport.storify.cache;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class TimeoutCacheEntry<V> implements Serializable {

        private final ZonedDateTime cachedAt = ZonedDateTime.now();
        private final ZonedDateTime staleAt;
        private final V value;
        TimeoutCacheEntry(V value, long staleMillis) {
            this.value = value;
            this.staleAt = cachedAt.plus(staleMillis, ChronoUnit.MILLIS);
        }
        public V getValue() {
            return value;
        }
        public ZonedDateTime getCachedAt(){
            return this.cachedAt;
        }
        public boolean isStale() {
            return ZonedDateTime.now().isAfter(staleAt);
        }
}
