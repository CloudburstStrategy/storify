package com.paxport.storify.mapping;


import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import com.paxport.storify.Storify;
import com.paxport.storify.annotation.AnnotationUtils;
import com.paxport.storify.annotation.Id;
import com.paxport.storify.annotation.IgnoreLoad;
import com.paxport.storify.annotation.NullBuilder;
import com.paxport.storify.annotation.UseMapper;
import com.paxport.storify.mapping.mappers.BooleanMapper;
import com.paxport.storify.mapping.mappers.DoubleMapper;
import com.paxport.storify.mapping.mappers.EnumMapper;
import com.paxport.storify.mapping.mappers.FloatMapper;
import com.paxport.storify.mapping.mappers.IntegerMapper;
import com.paxport.storify.mapping.mappers.LongMapper;
import com.paxport.storify.mapping.mappers.StringMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityMapper implements EntityBuilder, ObjectBuilder {
    private final static Logger logger = LoggerFactory.getLogger(EntityMapper.class);

    private Map<Class<?>,PropertyMapper<?>> classToPropertyMappers = new Hashtable<>();
    private Map<Class<?>,EntityBuilder> classToEntityBuilders = new Hashtable<>();
    private Map<Class<?>,ObjectBuilder> classToObjectBuilders = new Hashtable<>();
    private EnumMapper enumMapper = new EnumMapper();

    public EntityMapper(){
        registerDefaultMappers();
    }

    public EntityMapper registerPropertyMapper(PropertyMapper<?> mapper, Class<?>... classes){
        for (Class<?> cls : classes) {
            classToPropertyMappers.put(cls,mapper);
        }
        return this;
    }

    public EntityMapper registerEntityBuilder(EntityBuilder builder, Class<?>... classes){
        for (Class<?> cls : classes) {
            classToEntityBuilders.put(cls,builder);
        }
        return this;
    }

    public EntityMapper registerObjectBuilder(ObjectBuilder builder, Class<?>... classes){
        for (Class<?> cls : classes) {
            classToObjectBuilders.put(cls,builder);
        }
        return this;
    }

    protected EntityMapper registerDefaultMappers() {
        registerPropertyMapper(new StringMapper(),String.class);
        registerPropertyMapper(new BooleanMapper(),Boolean.class, Boolean.TYPE);
        registerPropertyMapper(new LongMapper(),Long.class, Long.TYPE);
        registerPropertyMapper(new IntegerMapper(),Integer.class, Integer.TYPE);
        registerPropertyMapper(new DoubleMapper(),Double.class, Double.TYPE);
        registerPropertyMapper(new FloatMapper(),Float.class, Float.TYPE);
        return this;
    }

    @Override
    public Entity buildEntity(Class<?> type, Object pojo, Storify storify) {
        if ( classToEntityBuilders.containsKey(type) ) {
            return classToEntityBuilders.get(type).buildEntity(type,pojo,storify);
        }
        return buildEntityUsingIntrospection(type, pojo, storify);
    }

    protected Entity buildEntityUsingIntrospection(Class<?> type, Object pojo, Storify storify) {
        try {
            Key key = key(type, pojo, storify);
            Entity.Builder builder = Entity.builder(key);
            for (PropertyDescriptor prop : introspectProperties(type)) {
                String propName = prop.getName();
                Object value = prop.getReadMethod().invoke(pojo);
                if (value == null ) {
                    if(logger.isDebugEnabled()) {
                        logger.debug("type " + type.getSimpleName() + " setting " + propName + " on entity to null");
                    }
                    builder.setNull(propName);
                }
                else {
                    PropertyMapper propMapper = findPropertyMapper(prop);
                    if ( logger.isDebugEnabled() ){
                        logger.debug("type " + type.getSimpleName() + " setting " + propName + " on entity");
                    }
                    propMapper.mapToEntity(builder,propName,value,prop.getReadMethod());
                }
            }
            return builder.build();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> E buildObject(Class<E> type, Entity entity, Storify storify) {
        if ( classToObjectBuilders.containsKey(type) ) {
            return classToObjectBuilders.get(type).buildObject(type,entity,storify);
        }
        com.paxport.storify.annotation.Entity ann = type.getAnnotation(com.paxport.storify.annotation.Entity.class);
        if (ann != null && !ann.builderClass().equals(NullBuilder.class) ) {
            return buildObjectWithBuilder(ann.builderClass(),type,entity);
        }
        return buildObjectUsingIntrospection(type, entity);
    }

    private <E> E buildObjectWithBuilder(Class<?> builderClass, Class<E> type, Entity entity) {
        try {
            Object builder;
            try {
                builder = builderClass.newInstance();
            }
            catch (Exception e) {
                builder = builderClass.getEnclosingClass().getMethod("builder").invoke(null);
            }
            for (PropertyDescriptor prop : introspectProperties(type)) {
                String propName = prop.getName();
                if ( shouldLoadProperty(entity,prop) ) {
                    PropertyMapper<?> propMapper = findPropertyMapper(prop);
                    Method writeMethod = builder.getClass().getMethod(propName,prop.getPropertyType());
                    Object value = propMapper.mapFromEntity(entity,propName,writeMethod);
                    if ( value != null && writeMethod != null ) {
                        if ( logger.isDebugEnabled() ){
                            logger.debug("type " + type.getSimpleName() + " setting " + propName + " on builder to " + value);
                        }
                        writeMethod.invoke(builder,value);
                    }
                }
            }
            return (E) builderClass.getMethod("build").invoke(builder);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean shouldLoadProperty (Entity entity, PropertyDescriptor prop) {
        String propName = prop.getName();
        if ( !entity.contains( propName) ) {
            if(logger.isDebugEnabled()){
                logger.debug("Not loading " + propName + " as entity does not contain it");
            }
            return false;
        }
        else if ( entity.isNull( propName ) ) {
            if(logger.isDebugEnabled()){
                logger.debug("Not loading " + propName + " as it is null");
            }
            return false;
        }
        else if (AnnotationUtils.isAnnotationPresent(prop.getReadMethod(),IgnoreLoad.class)) {
            if(logger.isDebugEnabled()){
                logger.debug("Not loading " + propName + " due to @IgnoreLoad");
            }
            return false;
        }
        else {
            if(logger.isDebugEnabled()){
                logger.debug("Will Load " + propName);
            }
            return true;
        }
    }

    protected <E> E buildObjectUsingIntrospection(Class<E> type, Entity entity) {
        try {
            E result = type.newInstance();
            for (PropertyDescriptor prop : introspectProperties(type)) {
                String propName = prop.getName();
                if ( shouldLoadProperty(entity,prop) ) {
                    PropertyMapper<?> propMapper = findPropertyMapper(prop);
                    Method writeMethod = findWriteMethod(type,prop);
                    Object value = propMapper.mapFromEntity(entity,propName,writeMethod);
                    if ( value != null && writeMethod != null ) {
                        if ( logger.isDebugEnabled() ){
                            logger.debug("type " + type.getSimpleName() + " setting " + propName + " on pojo to " + value);
                        }
                        writeMethod.invoke(result,value);
                    }
                }
            }
            return result;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private <E> Method findWriteMethod(Class<E> type, PropertyDescriptor prop) {
        if ( prop.getWriteMethod() != null ){
            return prop.getWriteMethod();
        }
        else {
            String pn = prop.getName();
            String setterName = "set" + pn.substring(0,1).toUpperCase() + pn.substring(1);
            try {
                return type.getMethod(setterName,prop.getPropertyType());
            } catch (NoSuchMethodException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to find write method for prop " + pn + " on type " + type.getSimpleName() );
                }
            }
            return null;
        }
    }

    protected PropertyMapper findPropertyMapper(PropertyDescriptor prop) {
        UseMapper specificMapper = AnnotationUtils.findAnnotation(prop.getReadMethod(),UseMapper.class);
        PropertyMapper propMapper;
        if ( specificMapper != null ) {
            propMapper = ensurePropertyMapper(specificMapper.value());
        }
        else {
            Class<?> propertyType = prop.getPropertyType();
            propMapper = classToPropertyMappers.get(propertyType);
            if ( propMapper == null && propertyType.isEnum() ) {
                return enumMapper;
            }
            if ( propMapper == null ) {
                throw new RuntimeException("Failed to find property mapper for " + prop.getName() + " of type: " + propertyType.getName());
            }
        }
        return propMapper;
    }

    private PropertyMapper ensurePropertyMapper(Class<? extends PropertyMapper<?>> value) {
        Optional<PropertyMapper<?>> propMapper = classToPropertyMappers.values().stream()
                .filter(pm -> pm.getClass().getName().equals(value.getName()))
                .findFirst();
        if ( propMapper.isPresent() ) {
            return propMapper.get();
        }
        try {
            return value.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Key key(Class<?> type, Object pojo, Storify storify) {
        try {
            for (PropertyDescriptor prop : introspectProperties(type)) {
                if (AnnotationUtils.isAnnotationPresent(prop.getReadMethod(), Id.class)) {
                    Object value = prop.getReadMethod().invoke(pojo);
                    if ( String.class.equals(prop.getPropertyType()) ) {
                        if ( value == null ) {
                            throw new RuntimeException("String key should be set on pojo as @Id property " + prop.getName() + " but is not");
                        }
                        else{
                            return storify.newKey(type,(String)value);
                        }
                    }
                    else{
                        return storify.newKey(type,(Long)value);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Failed to find @Id on any getter method of type: " + type.getName() );
    }

    protected List<PropertyDescriptor> introspectProperties(Class<?> type) {
        BeanInfo beanInfo = introspect(type);
        return Arrays.asList(beanInfo.getPropertyDescriptors()).stream()
                .filter(p -> !"class".equals(p.getName()))
                .collect(Collectors.toList());
    }

    private BeanInfo introspect(Class<?> type) {
        try {
            return Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }
}
