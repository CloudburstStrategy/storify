package com.paxport.storify.mapping.mappers;

import com.google.cloud.datastore.Entity;

import java.lang.reflect.Method;

public class FloatMapper extends AbstractPropertyMapper<Float> {
    @Override
    public void mapToEntity(Entity.Builder builder, String propName, Float value, Method getter) {
        builder.set(propName,value);
    }

    @Override
    public Float mapFromEntity(Entity entity, String propName, Method setter) {
        return isNull(entity,propName) ? null : (float)entity.getDouble(propName);
    }
}
