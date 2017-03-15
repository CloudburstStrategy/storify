package com.cloudburst.storify.mapping.mappers;

import com.google.cloud.datastore.Entity;

import com.cloudburst.storify.mapping.PropertyMapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by ajchesney on 30/08/2016.
 */
public class EnumMapper implements PropertyMapper {
    @Override
    public void mapToEntity(Entity.Builder builder, String propName, Object value, Method getter) {
        builder.set(propName,((Enum)value).name());
    }

    @Override
    public Object mapFromEntity(Entity entity, String propName, Method setter) {
        try {
            return setter.getParameterTypes()[0].getMethod("valueOf", String.class).invoke(null,entity.getString(propName));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
