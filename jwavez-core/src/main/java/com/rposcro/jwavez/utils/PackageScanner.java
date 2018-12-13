package com.rposcro.jwavez.utils;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PackageScanner {

  public List<Class> findAllClasses(String basePackage, boolean scanSubpackages) throws IOException {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    assert classLoader != null;
    String path = basePackage.replace('.', '/');

    List<File> basePackageEntries = Collections.list(classLoader.getResources(path)).stream()
        .map(URL::getFile)
        .map(File::new)
        .collect(Collectors.toList());

    return basePackageEntries.stream()
        .map(entry -> scanPackageTreeForClasses(entry, basePackage, scanSubpackages))
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  private List<Class<?>> scanPackageTreeForClasses(File packageFile, String packageName, boolean scanSubpackages) {
    if (!packageFile.exists() || !packageFile.isDirectory()) {
      return Collections.emptyList();
    }

    List<File> subPackages = new LinkedList<>();
    List<Class<?>> classesList = Arrays.stream(packageFile.listFiles())
        .filter(file -> {
          if (file.isDirectory()) {
            subPackages.add(file);
          }
          return file.isFile() && file.getName().endsWith(".class");
        })
        .map(classFile -> loadClass(packageName, classFile))
        .collect(Collectors.toList());

    if (scanSubpackages) {
      subPackages.stream()
          .forEach(subPackageFile -> classesList.addAll(
              scanPackageTreeForClasses(subPackageFile, packageName + '.' + subPackageFile.getName(), scanSubpackages)));
    }

    return classesList;
  }

  private Class<?> loadClass(String packageName, File classFile) {
    try {
      return Class.forName(className(packageName, classFile));
    } catch(ClassNotFoundException e) {
      throw new SerialException("Failed to load class: " + packageName + '.' + classFile);
    }
  }

  private String className(String packageName, File classFile) {
    return packageName + '.' + classFile.getName().substring(0, classFile.getName().length() - 6);
  }
}
