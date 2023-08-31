package com.rposcro.jwavez.core.utils;

import com.rposcro.jwavez.core.exceptions.CodeIntegrityException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.reflections.Reflections;

public class PackageScanner {

    public <T> Set<T> instantiateAll(Set<Class<? extends T>> classes) {
        return classes.stream()
                .map(this::instantiateClass)
                .collect(Collectors.toSet());
    }

    public <T> Set<Class<? extends T>> findAllClassifiedClasses(Class<? extends Annotation> annotation, Class<T> classType, String... basePackages) {
        return Stream.of(basePackages)
                .map(basePackage -> new Reflections(basePackage).getSubTypesOf(classType))
                .flatMap(classes -> classes.stream())
                .filter(clazz -> Stream.of(clazz.getModifiers()).noneMatch(Modifier::isAbstract))
                .filter(clazz -> {
                    boolean annPresent = clazz.isAnnotationPresent(annotation);
                    boolean isOfType = classType.isAssignableFrom(clazz);
                    if (annPresent && !isOfType) {
                        throw new CodeIntegrityException(String.format("Class %s is annotated as %s, but is not of type %s!", clazz, annotation, classType));
                    } else if (!annPresent && isOfType) {
                        throw new CodeIntegrityException(String.format("Class %s is of type %s, but is not annotated as %s!", clazz, annotation, classType));
                    } else {
                        return annPresent && isOfType;
                    }
                })
                .collect(Collectors.toSet());
    }

    public Set<Class<?>> findAllClassesWithAnnotation(Class<? extends Annotation> annotation, String... basePackages) {
        return Stream.of(basePackages)
                .map(packagePath -> new Reflections(packagePath).getTypesAnnotatedWith(annotation))
                .flatMap(classes -> classes.stream())
                .collect(Collectors.toSet());
    }

    public <T> Set<Class<? extends T>> findAllClassesOfType(Class<T> classType, String... basePackages) {
        return Stream.of(basePackages)
                .map(packagePath -> new Reflections(packagePath).getSubTypesOf(classType))
                .flatMap(classes -> classes.stream())
                .collect(Collectors.toSet());
    }

    private <T> T instantiateClass(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CodeIntegrityException("Cannot instantiateClass object of class: " + clazz, e);
        }
    }
}
