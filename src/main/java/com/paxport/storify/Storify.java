package com.paxport.storify;


import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

import com.paxport.storify.annotation.AnnotationUtils;
import com.paxport.storify.mapping.EntityMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Storify {

    private final static Logger logger = LoggerFactory.getLogger(Storify.class);

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
        Optional<com.paxport.storify.annotation.Entity> ann = AnnotationUtils.findAnnotation(type,
                com.paxport.storify.annotation.Entity.class);
        if ( ann.isPresent() && !ann.get().name().equals("")) {
            return keyFactory.kind(ann.get().name());
        }
        else {
            return keyFactory.kind(type.getSimpleName());
        }
    }

    public Datastore datastore() {
        return this.datastore;
    }

    public <E> Optional<E> load(Class<E> type, String key){
        Key k = keyFactory(type).newKey(key);
        Entity entity = datastore.get(k);
        if ( entity == null ) {
            if ( logger.isDebugEnabled() ){
                logger.debug("no entity found for key: " + k);
            }
            return Optional.empty();
        }
        return Optional.of(mapper.buildObject(type,entity,this));
    }


    public Entity put(Object pojo) {
        Entity entity = mapper.buildEntity(pojo.getClass(),pojo,this);
        entity =  datastore.put(entity);
        if ( logger.isDebugEnabled() ){
            logger.debug("entity put successful " + entity);
        }
        return entity;
    }


}
