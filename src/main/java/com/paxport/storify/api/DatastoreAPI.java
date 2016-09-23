package com.paxport.storify.api;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

/**
 * Contains the low level functions storify needs from the datastore api
 *
 * This can be mocked for testing purposes
 */
public interface DatastoreAPI {

    Key newKey(String kind, String name);

    Key newKey(String kind, Long id);

    Entity put(Entity entity);

    Entity get(Key key);

    void delete(Key key);

}
