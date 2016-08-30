package com.paxport.storify.annotation;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnnotationUtils {

    public static <A extends Annotation> Optional<A> findAnnotation(Class cls, Class<A> annotationClass) {
        do {
            A annotation = (A) cls.getAnnotation(annotationClass);
            if ( annotation != null ) {
                return Optional.of(annotation);
            }
        } while ( (cls = cls.getSuperclass()) != null );
        return Optional.empty();
    }

    public static boolean isAnnotationPresent(Method method, Class<? extends Annotation> annotationClass){
        return findAnnotation(method, annotationClass) != null;
    }

    public static <T extends Annotation> T findAnnotation(Method method, Class<T> annotationClass) {
        for (Class<?> cls : classHierarchy(method.getDeclaringClass())) {
            try {
                method = cls.getMethod(method.getName(), method.getParameterTypes());
                if ( method.isAnnotationPresent(annotationClass) ) {
                    return method.getAnnotation(annotationClass);
                }
            } catch (NoSuchMethodException e) {
            }
        }
        return null;
    }

    private static List<Class<?>> classHierarchy(Class cls) {
        List<Class<?>> result = new ArrayList<>();
        do {
            result.add(cls);
        } while ((cls = cls.getSuperclass()) != null);
        return result;
    }
}
