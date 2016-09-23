package com.paxport.storify.api;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

public class RESTDatastoreAPI implements DatastoreAPI {

    private Datastore datastore;

    public RESTDatastoreAPI(){
        this (DatastoreOptions.defaultInstance().service());
    }

    public RESTDatastoreAPI(Datastore datastore){
        this.datastore = datastore;
    }

    @Override
    public Key newKey(String kind, String name) {
        return datastore.newKeyFactory().kind(kind).newKey(name);
    }

    @Override
    public Key newKey(String kind, Long id) {
        return datastore.newKeyFactory().kind(kind).newKey(id);
    }

    @Override
    public Entity put(Entity entity) {
        return datastore.put(entity);
    }

    @Override
    public Entity get(Key key) {
        return datastore.get(key);
    }

    @Override
    public void delete(Key key) {
        datastore.delete(key);
    }
}
