package com.rposcro.jwavez.core.utils;

import com.rposcro.jwavez.core.exceptions.CodeIntegrityException;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PackageScanner {

  public <T> List<T> instantiateAll(List<Class<T>> classes) {
    return classes.stream()
        .map(this::instantiateClass)
        .collect(Collectors.toList());
  }

  public <T> List<Class<T>> findAllClassifiedClasses(String basePackage, boolean scanSubpackages, Class<? extends Annotation> annotation, Class<T> classType) {
    List<Class<T>> classList = findAllClasses(basePackage, scanSubpackages);
    return classList.stream()
        .filter(clazz -> {
          boolean annPresent = clazz.isAnnotationPresent(annotation);
          boolean isOfType = classType.isAssignableFrom(clazz);
          if (annPresent && !isOfType) {
            throw new CodeIntegrityException(String.format("Class %s is annotated as %s, but is not of type %s!", clazz, annotation, classType));
          } else if (!annPresent && isOfType) {
            throw new CodeIntegrityException(String.format("Class %s is of type %s, but is not annotate as %s!", clazz, annotation, classType));
          } else {
            return annPresent && isOfType;
          }
        })
        .collect(Collectors.toList());
  }

  public <T> List<Class<T>> findAllClassesWithAnnotation(String basePackage, boolean scanSubpackages, Class<? extends Annotation> annotation) {
    List<Class<T>> classList = findAllClasses(basePackage, scanSubpackages);
    return classList.stream()
        .filter(clazz -> clazz.isAnnotationPresent(annotation))
        .collect(Collectors.toList());
  }

  public <T> List<Class<T>> findAllClasses(String basePackage, boolean scanSubpackages) {
    try {
      List<File> basePackageEntries = packageEntries(basePackage);
      List<Class<T>> classList = new ArrayList<>();

      basePackageEntries.stream()
          .forEach(packageEntry -> {
            List<Class<T>> packageClasses = scanPackageTreeForClasses(packageEntry, basePackage, scanSubpackages);
            classList.addAll(packageClasses);
          });
      return classList;
    } catch(IOException e) {
      throw new CodeIntegrityException(String.format("Failed to scan package(s): %s, recursive: %s.", basePackage, scanSubpackages), e);
    }
  }

  private <T> List<Class<T>> scanPackageTreeForClasses(File packageFile, String packageName, boolean scanSubpackages) {
    if (!packageFile.exists() || !packageFile.isDirectory()) {
      return Collections.emptyList();
    }

    List<File> subPackages = new LinkedList<>();
    List<Class<T>> classesList = Arrays.stream(packageFile.listFiles())
        .filter(file -> {
          if (file.isDirectory()) {
            subPackages.add(file);
          }
          return file.isFile() && file.getName().endsWith(".class");
        })
        .map(classFile -> (Class<T>) loadClass(packageName, classFile))
        .collect(Collectors.toList());

    if (scanSubpackages) {
      subPackages.stream()
          .forEach(subPackageFile -> classesList.addAll(
              scanPackageTreeForClasses(subPackageFile, packageName + '.' + subPackageFile.getName(), scanSubpackages)));
    }

    return classesList;
  }

  private <T> Class<T> loadClass(String packageName, File classFile) {
    try {
      return (Class<T>) Class.forName(fullClassName(packageName, classFile));
    } catch(ClassNotFoundException e) {
      throw new CodeIntegrityException("Failed to load class: " + packageName + '.' + classFile);
    }
  }

  private <T> T instantiateClass(Class<T> clazz) {
    try {
      return clazz.newInstance();
    } catch(InstantiationException | IllegalAccessException e) {
      throw new CodeIntegrityException("Cannot instantiateClass object of class: " + clazz, e);
    }
  }

  private String fullClassName(String packageName, File classFile) {
    return packageName + '.' + classFile.getName().substring(0, classFile.getName().length() - 6);
  }

  private List<File> packageEntries(String packagePath) throws IOException {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    assert classLoader != null;
    String path = packagePath.replace('.', '/');

    return Collections.list(classLoader.getResources(path)).stream()
        .map(URL::getFile)
        .map(File::new)
        .collect(Collectors.toList());
  }
}
