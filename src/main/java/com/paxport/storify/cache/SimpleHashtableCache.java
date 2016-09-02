package com.paxport.storify.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Optional;

/**
 * For testing etc
 */
public class SimpleHashtableCache implements SimpleCache {

    private Hashtable<String,Object> map = new Hashtable<>();

    @Override
    public <E> Optional<E> get(String key) {
        return Optional.ofNullable((E)map.get(key));
    }

    @Override
    public void put(String key, Object value) {
        testSerialisation(value);
        map.put(key,value);
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }


    /**
     * Check that we can serialize the object as would occur if using memcache
     * @param value
     */
    private void testSerialisation(Object value) {
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
            ObjectOutputStream out = new ObjectOutputStream(bos) ;
            out.writeObject(value);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialise object so won't work with Memcache: " + value, e);
        }
    }
}
