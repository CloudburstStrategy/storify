package com.paxport.storify.mapping;

import com.google.cloud.datastore.Entity;

import java.lang.reflect.Method;

/**
 * Map Datastore field into Object property
 *
 *
 *
 */
public interface PropertyMapper<E> {

    /**
     * Call the appropriate set method on the builder after doing any necessary conversion
     * of the property value
     *
     * @param builder
     * @param propName
     * @param getter - in case you need type info
     */
    void mapToEntity ( Entity.Builder builder, String propName, E value, Method getter );

    /**
     * Grab the property from the entity and convert to the desired type
     *
     * @param entity
     * @param propName
     * @param setter
     * @return the value you want to be set on the target object
     */
    E mapFromEntity ( Entity entity, String propName, Method setter );



}
