package com.paxport.storify.mapping.mappers;

import com.google.cloud.datastore.Entity;

import java.lang.reflect.Method;

public class IntegerMapper extends AbstractPropertyMapper<Integer> {
    @Override
    public void mapToEntity(Entity.Builder builder, String propName, Integer value, Method getter) {
        builder.set(propName,value);
    }

    @Override
    public Integer mapFromEntity(Entity entity, String propName, Method setter) {
        return isNull(entity,propName) ? null : (int) entity.getLong(propName);
    }
}
