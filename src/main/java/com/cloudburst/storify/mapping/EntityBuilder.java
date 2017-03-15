package com.cloudburst.storify.mapping;


import com.google.cloud.datastore.Entity;

import com.cloudburst.storify.Storify;

public interface EntityBuilder {

    Entity buildEntity(Class<?> kind, Object pojo, Storify storify);

}
