package com.paxport.storify.annotation;

import com.paxport.storify.mapping.PropertyMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Place this annotation on the getter method in order to register and use the associated property mapper</p>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface UseMapper
{
    Class<? extends PropertyMapper<?>> value();
}