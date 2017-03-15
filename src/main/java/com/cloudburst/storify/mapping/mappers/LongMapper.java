package com.cloudburst.storify.mapping.mappers;

import com.google.cloud.datastore.Entity;

import java.lang.reflect.Method;

public class LongMapper extends AbstractPropertyMapper<Long> {
    @Override
    public void mapToEntity(Entity.Builder builder, String propName, Long value, Method getter) {
        builder.set(propName,value);
    }

    @Override
    public Long mapFromEntity(Entity entity, String propName, Method setter) {
        return isNull(entity,propName) ? null : entity.getLong(propName);
    }
}
