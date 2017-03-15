package com.cloudburst.storify;

import com.cloudburst.storify.api.MockDatastoreAPI;
import com.cloudburst.storify.cache.SimpleMemcache;
import com.cloudburst.storify.mapping.EntityMapper;

/**
 * Replace the remote parts only
 */
public class MockStorify extends Storify {

    public MockStorify(){
        super(new MockDatastoreAPI(),new EntityMapper(), new SimpleMemcache());
    }
}
