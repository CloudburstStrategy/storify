package com.paxport.storify;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import com.paxport.storify.api.DatastoreAPI;
import com.paxport.storify.cache.SimpleHashtableCache;
import com.paxport.storify.examples.CachedEntity;
import com.paxport.storify.mapping.EntityMapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

//Let's import Mockito statically so that the code looks clearer

public class CacheTests {

    @Mock
    private DatastoreAPI datastore;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBasicPutAndThenLoad() {
        SimpleHashtableCache mockCache = new SimpleHashtableCache();

        CachedEntity entity = new CachedEntity().setName("testkey").setValue("testvalue").setLongValue(123);
        Storify storify = new Storify(datastore,new EntityMapper(),mockCache);

        Key key = Key.builder("test","CachedEntity","testkey").build();
        Entity e = Entity.builder(key)
                .set("name","testkey")
                .set("value","testvalue")
                .set("longValue",123)
                .build();

        when(datastore.newKey("CachedEntity","testkey")).thenReturn(key);
        when(datastore.put(any())).thenReturn(e);
        storify.put(entity);
        String cacheKey = key.toUrlSafe();

        Assert.assertEquals(true,mockCache.get(cacheKey).isPresent());

        CachedEntity test = storify.load(CachedEntity.class,"testkey").get();
        Assert.assertEquals("testkey", test.getName());
        Assert.assertEquals("testvalue", test.getValue());
        Assert.assertEquals(123, test.getLongValue());

        verify(datastore,times(2)).newKey("CachedEntity","testkey");
        verify(datastore).put(any());

        // no call to get on datastore as it should come from cache
        verifyNoMoreInteractions(datastore);

    }

    @Test
    public void testNoResultsAreCached() {
        SimpleHashtableCache mockCache = new SimpleHashtableCache();

        Storify storify = new Storify(datastore,new EntityMapper(),mockCache);

        Key key = Key.builder("test","CachedEntity","testkey_notthere").build();
        when(datastore.newKey("CachedEntity","testkey_notthere")).thenReturn(key);
        when(datastore.get(key)).thenReturn(null);

        Optional<CachedEntity> test = storify.load(CachedEntity.class,"testkey_notthere");
        Assert.assertEquals(false,test.isPresent());

        Optional<CachedEntity> test2 = storify.load(CachedEntity.class,"testkey_notthere");
        Assert.assertEquals(false,test2.isPresent());

        verify(datastore,times(2)).newKey("CachedEntity","testkey_notthere");
        verify(datastore).get(key);

        // no further call to get on datastore as no results should come from cache
        verifyNoMoreInteractions(datastore);
    }

}
