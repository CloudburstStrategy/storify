package com.cloudburst.storify.mapping.mappers;

import com.google.cloud.datastore.Entity;

import java.lang.reflect.Method;

public class BooleanMapper extends AbstractPropertyMapper<Boolean> {
    @Override
    public void mapToEntity(Entity.Builder builder, String propName, Boolean value, Method getter) {
        builder.set(propName,value);
    }

    @Override
    public Boolean mapFromEntity(Entity entity, String propName, Method setter) {
        return isNull(entity,propName) ? null : entity.getBoolean(propName);
    }
}
