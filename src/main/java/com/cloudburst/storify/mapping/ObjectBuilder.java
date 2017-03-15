package com.cloudburst.storify.mapping;

import com.google.cloud.datastore.Entity;

import com.cloudburst.storify.Storify;


public interface ObjectBuilder {

    <E> E buildObject(Class<E> kind, Entity entity, Storify storify);

}
