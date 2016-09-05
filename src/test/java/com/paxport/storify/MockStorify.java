package com.paxport.storify;

import com.paxport.storify.api.MockDatastoreAPI;
import com.paxport.storify.cache.SimpleMemcache;
import com.paxport.storify.mapping.EntityMapper;

/**
 * Replace the remote parts only
 */
public class MockStorify extends Storify {

    public MockStorify(){
        super(new MockDatastoreAPI(),new EntityMapper(), new SimpleMemcache());
    }
}
