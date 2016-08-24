package com.paxport.storify.mapping;


import com.google.cloud.datastore.Entity;

import com.paxport.storify.Storify;

public interface EntityBuilder {

    Entity buildEntity(Class<?> kind, Object pojo, Storify storify);

}
