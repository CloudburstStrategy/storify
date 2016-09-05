package com.paxport.storify;


import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

import com.paxport.storify.annotation.AnnotationUtils;
import com.paxport.storify.annotation.Cache;
import com.paxport.storify.cache.EntityCache;
import com.paxport.storify.cache.SimpleCache;
import com.paxport.storify.cache.SimpleMemcache;
import com.paxport.storify.mapping.EntityMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Storify {

    private final static Logger logger = LoggerFactory.getLogger(Storify.class);

    private final static long ONE_WEEK_MILLIS = 7 * 24 * 60 * 60 * 1000;

    private static Storify STORIFY;

    private Datastore datastore;

    private KeyFactory keyFactory;

    private EntityMapper mapper;

    private EntityCache entityCache;

    public Storify(Datastore datastore, EntityMapper entityMapper, SimpleCache cache){
        this.datastore = datastore;
        this.keyFactory = datastore.newKeyFactory();
        this.mapper = entityMapper;
        this.entityCache = new EntityCache(cache);
    }

    public Storify(){
        this(DatastoreOptions.defaultInstance().service(),new EntityMapper(), new SimpleMemcache());
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
        Optional<Entity> cachedEntity = checkCacheIfAppropriate(type,k);
        Entity entity = cachedEntity.orElse(datastore.get(k));
        if ( entity == null ) {
            if ( logger.isDebugEnabled() ){
                logger.debug("no entity found for key: " + k);
            }
            return Optional.empty();
        }
        return Optional.of(mapper.buildObject(type,entity,this));
    }

    protected <E> Optional<Entity> checkCacheIfAppropriate(Class<E> type, Key key) {
        Optional<Cache> annotation = AnnotationUtils.findAnnotation(type,Cache.class);
        if ( annotation.isPresent() ) {
            Cache cacheConfig = annotation.get();
            Optional<Entity> result = entityCache.get(key.toUrlSafe());
            if ( logger.isDebugEnabled() ) {
                if ( result.isPresent() ) {
                    logger.debug("Entity cache hit for type: " + type.getName() );
                }
                else {
                    logger.debug("Entity cache miss for type: " + type.getName() );
                }
            }
            return result;
        }
        else {
            return Optional.empty();
        }
    }


    public Entity put(Object pojo) {
        Entity entity = mapper.buildEntity(pojo.getClass(),pojo,this);
        entity =  datastore.put(entity);
        if ( logger.isDebugEnabled() ){
            logger.debug("entity put successful " + entity);
        }

        cacheEntityIfAppropriate(pojo, entity);

        return entity;
    }

    protected void cacheEntityIfAppropriate(Object pojo, Entity entity) {
        Optional<Cache> annotation = AnnotationUtils.findAnnotation(pojo.getClass(),Cache.class);
        if ( annotation.isPresent() ) {
            Cache cacheConfig = annotation.get();
            int expirationSecs = cacheConfig.expirationSeconds();
            long staleMillis = expirationSecs==0?ONE_WEEK_MILLIS:expirationSecs*1000;
            String cacheKey = entity.key().toUrlSafe();
            entityCache.put(cacheKey,entity,staleMillis);
            if ( logger.isDebugEnabled() ) {
                logger.debug("put entity with key " + cacheKey + " into entity cache with staleMillis = " + staleMillis);
            }
        }
    }
}
