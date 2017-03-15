package com.cloudburst.storify.api;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import java.util.Hashtable;
import java.util.Map;

/**
 * Useful for testing, this will implement basic datastore ops with a simple hashtable
 */
public class MockDatastoreAPI implements DatastoreAPI {

    private Map<Key,Entity> map = new Hashtable<>();

    @Override
    public Key newKey(String kind, String name) {
        return Key.builder("mock-key",kind,name).build();
    }

    @Override
    public Key newKey(String kind, Long id) {
        return Key.builder("mock-key",kind,id).build();
    }

    @Override
    public Entity put(Entity entity) {
        return map.put(entity.key(),entity);
    }

    @Override
    public Entity get(Key key) {
        return map.get(key);
    }

    @Override
    public void delete(Key key) {
        map.remove(key);
    }
}
