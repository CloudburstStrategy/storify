package com.cloudburst.storify.cache;

import com.google.cloud.datastore.Entity;

/**
 * Cache entities to memcache
 */
public class EntityCache extends TimeoutCache<Entity> {

    public EntityCache() {
        super();
    }

    public EntityCache(SimpleCache delegate) {
        super(delegate);
    }
}
