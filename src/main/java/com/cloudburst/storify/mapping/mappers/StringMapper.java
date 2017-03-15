package com.cloudburst.storify.mapping.mappers;

import com.google.cloud.datastore.Entity;

import java.lang.reflect.Method;

public class StringMapper extends AbstractPropertyMapper<String> {
    @Override
    public void mapToEntity(Entity.Builder builder, String propName, String value, Method getter) {
        builder.set(propName,value);
    }

    @Override
    public String mapFromEntity(Entity entity, String propName, Method setter) {
        return isNull(entity,propName) ? null : entity.getString(propName);
    }
}
