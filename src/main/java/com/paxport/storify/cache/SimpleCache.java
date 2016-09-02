package com.paxport.storify.cache;

import java.util.Optional;

/**
 * Created by ajchesney on 26/04/2016.
 */
public interface SimpleCache {

    <E> Optional<E> get(String key);

    void put(String key, Object value);

    void remove(String key);

}
