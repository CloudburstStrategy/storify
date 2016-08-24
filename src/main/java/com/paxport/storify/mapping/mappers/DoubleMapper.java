package com.paxport.storify.mapping.mappers;

import com.google.cloud.datastore.Entity;

import java.lang.reflect.Method;

public class DoubleMapper extends AbstractPropertyMapper<Double> {
    @Override
    public void mapToEntity(Entity.Builder builder, String propName, Double value, Method getter) {
        builder.set(propName,value);
    }

    @Override
    public Double mapFromEntity(Entity entity, String propName, Method setter) {
        return isNull(entity,propName) ? null : entity.getDouble(propName);
    }
}
