package com.cloudburst.storify.cache;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Simple Cache backed by memcache
 */
public class SimpleMemcache implements SimpleCache {
    private final static Logger logger = LoggerFactory.getLogger(SimpleMemcache.class);

    private MemcachedClient client;

    private int storageSeconds = 7 * 24 * 60 * 60; // 1 week

    public SimpleMemcache(){
        this(System.getenv("MEMCACHE_PORT_11211_TCP_ADDR"),System.getenv("MEMCACHE_PORT_11211_TCP_PORT"));
    }

    public SimpleMemcache(String addr, String port){
        if (null==addr) {
            addr = "localhost";
        }
        if(null==port) {
            port = "11211";
        }
        String connectStr = addr + ":" + port;
        logger.info("Binding to Memcache on " + connectStr);
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(connectStr));
        try {
            client = builder.build();
            logger.info("Successfully built memcache client");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MemcachedClient getClient() {
        return client;
    }

    public SimpleMemcache setClient(MemcachedClient client) {
        this.client = client;
        return this;
    }

    public int getStorageSeconds() {
        return storageSeconds;
    }

    public SimpleMemcache setStorageSeconds(int storageSeconds) {
        this.storageSeconds = storageSeconds;
        return this;
    }

    @Override
    public <E> Optional<E> get(String key) {
        try {
            return Optional.ofNullable(client.get(key));
        } catch (Exception e) {
            handleException("problem with get for key " + key, e);
        }
        return Optional.empty();
    }

    protected void handleException(String msg, Exception e) {
        if ( e instanceof MemcachedException ) {
            MemcachedException me = (MemcachedException) e;
            logger.warn(msg, e);
        }
        else {
            throw new RuntimeException(msg, e);
        }
    }

    @Override
    public void put(String key, Object value) {
        try {
            client.set(key,storageSeconds,value);
        } catch (Exception e) {
            handleException("problem with put with key " + key, e);
        }
    }

    @Override
    public void remove(String key) {
        try {
            client.delete(key);
        } catch (Exception e) {
            handleException("problem with delete for key " + key, e);
        }
    }
}
