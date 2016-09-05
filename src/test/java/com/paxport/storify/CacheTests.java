package com.paxport.storify;

import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;

import com.paxport.storify.cache.SimpleHashtableCache;
import com.paxport.storify.examples.CachedEntity;
import com.paxport.storify.mapping.EntityMapper;

import org.junit.Assert;
import org.junit.Test;


public class CacheTests {

    @Test
    public void testBasicPutAndThenLoad() {
        SimpleHashtableCache mockCache = new SimpleHashtableCache();

        CachedEntity entity = new CachedEntity().setName("testkey").setValue("testvalue").setLongValue(123);
        Storify storify = new Storify(DatastoreOptions.defaultInstance().service(),new EntityMapper(), mockCache);

        Entity e = storify.put(entity);
        String key = e.key().toUrlSafe();

        Assert.assertEquals(true,mockCache.get(key).isPresent());

        CachedEntity test = storify.load(CachedEntity.class,"testkey").get();
        Assert.assertEquals("testkey", test.getName());
        Assert.assertEquals("testvalue", test.getValue());
        Assert.assertEquals(123, test.getLongValue());

    }

}
