package com.paxport.storify;


import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

import com.paxport.storify.mapping.EntityMapper;

public class Storify {

    private static Storify STORIFY;

    private Datastore datastore;

    private KeyFactory keyFactory;

    private EntityMapper mapper;

    public Storify(Datastore datastore, EntityMapper entityMapper){
        this.datastore = datastore;
        this.keyFactory = datastore.newKeyFactory();
        this.mapper = entityMapper;
    }

    public Storify(){
        this(DatastoreOptions.defaultInstance().service(),new EntityMapper());
    }

    public static Storify sfy() {
        if ( STORIFY == null )
        {
            STORIFY = new Storify();
        }
        return STORIFY;
    }

    public KeyFactory keyFactory(Class<?> type) {
        return keyFactory.kind(type.getSimpleName());
    }

    public Datastore datastore() {
        return this.datastore;
    }

    public <E> E load(Class<E> type, String key){
        Key k = keyFactory(type).newKey(key);
        Entity entity = datastore.get(k);
        return mapper.buildObject(type,entity,this);
    }


    public Entity put(Object pojo) {
        Entity entity = mapper.buildEntity(pojo.getClass(),pojo,this);
        return datastore.put(entity);
    }


}
