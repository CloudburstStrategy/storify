package com.paxport.storify;


import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import com.paxport.storify.annotation.AnnotationUtils;
import com.paxport.storify.annotation.Cache;
import com.paxport.storify.api.DatastoreAPI;
import com.paxport.storify.api.RESTDatastoreAPI;
import com.paxport.storify.cache.EntityCache;
import com.paxport.storify.cache.SimpleCache;
import com.paxport.storify.cache.SimpleMemcache;
import com.paxport.storify.cache.TimeoutCacheEntry;
import com.paxport.storify.mapping.EntityMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Storify {

    private final static Logger logger = LoggerFactory.getLogger(Storify.class);

    private final static long ONE_WEEK_MILLIS = 7 * 24 * 60 * 60 * 1000;

    private static Storify STORIFY;

    private DatastoreAPI datastore;

    private EntityMapper mapper;

    private EntityCache entityCache;

    public Storify(DatastoreAPI datastore, EntityMapper entityMapper, SimpleCache cache){
        this.datastore = datastore;
        this.mapper = entityMapper;
        this.entityCache = new EntityCache(cache);
    }

    public Storify(){
        this(new RESTDatastoreAPI(),new EntityMapper(), new SimpleMemcache());
    }

    public static Storify sfy() {
        if ( STORIFY == null )
        {
            STORIFY = new Storify();
        }
        return STORIFY;
    }

    public Key newKey(Class<?> type, String name) {
        return datastore.newKey(kind(type),name);
    }

    public Key newKey(Class<?> type, Long id) {
        return datastore.newKey(kind(type),id);
    }

    /**
     * What kind in datastore is this type?
     *
     * Will look for kind() in @Entity or use simple type name
     *
     * @param type
     * @return
     */
    public String kind(Class<?> type) {
        Optional<com.paxport.storify.annotation.Entity> ann = AnnotationUtils.findAnnotation(type,
                com.paxport.storify.annotation.Entity.class);
        String kind = type.getSimpleName();
        if ( ann.isPresent() && !ann.get().kind().equals("")) {
            kind = ann.get().kind();
        }
        return kind;
    }

    public <E> Optional<E> load(Class<E> type, String key){
        Key k = newKey(type,key);
        Entity entity = null;
        if ( cacheStaleMillis(type).isPresent() ) {
            Optional<TimeoutCacheEntry<Entity>> cached = entityCache.getEntry(k.toUrlSafe());
            if ( cached.isPresent() ) {
                entity = cached.get().getValue();
                if ( entity == null ) {
                    // we know there is no result in datastore
                    return Optional.empty();
                }
            }
        }
        if ( entity == null ) {
            // no cached results so go to the datastore
            entity = datastore.get(k);
            cacheEntityIfAppropriate(type,k,entity);
        }
        if ( entity == null ) {
            if ( logger.isDebugEnabled() ){
                logger.debug("no entity found for key: " + k);
            }
            return Optional.empty();
        }
        return Optional.of(mapper.buildObject(type,entity,this));
    }

    private Optional<Long> cacheStaleMillis(Class<?> type) {
        Optional<Cache> cacheInfo = AnnotationUtils.findAnnotation(type,Cache.class);
        if ( cacheInfo.isPresent() ) {
            Cache cacheConfig = cacheInfo.get();
            int expirationSecs = cacheConfig.expirationSeconds();
            long staleMillis = expirationSecs==0?ONE_WEEK_MILLIS:expirationSecs*1000;
            return Optional.of(staleMillis);
        }
        else {
            return Optional.empty();
        }
    }

    protected Optional<Entity> checkCacheIfAppropriate(Class<?> type, Key key) {
        if ( cacheStaleMillis(type).isPresent() ) {
            Optional<Entity> result = entityCache.get(key.toUrlSafe());
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

        cacheEntityIfAppropriate(pojo.getClass(), entity.key(), entity);

        return entity;
    }

    protected void cacheEntityIfAppropriate(Class<?> type, Key key, Entity entity) {
        Optional<Long> staleMillis = cacheStaleMillis(type);
        if ( staleMillis.isPresent() ) {
            String cacheKey = key.toUrlSafe();
            entityCache.put(cacheKey,entity,staleMillis.get());
            if ( logger.isDebugEnabled() ) {
                logger.debug("put entity with key " + cacheKey + " into entity cache with staleMillis = " + staleMillis);
            }
        }
    }
}
