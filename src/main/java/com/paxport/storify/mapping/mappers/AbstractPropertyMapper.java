package com.paxport.storify.mapping.mappers;

import com.google.cloud.datastore.Entity;

import com.paxport.storify.mapping.PropertyMapper;


public abstract class AbstractPropertyMapper<E> implements PropertyMapper<E> {

    protected boolean isNull (Entity entity, String propName) {
        return !entity.contains(propName) || entity.isNull(propName);
    }
}
