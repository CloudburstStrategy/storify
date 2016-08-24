package com.paxport.storify.mapping;

import com.google.cloud.datastore.Entity;

import com.paxport.storify.Storify;


public interface ObjectBuilder {

    <E> E buildObject(Class<E> kind, Entity entity, Storify storify);

}
