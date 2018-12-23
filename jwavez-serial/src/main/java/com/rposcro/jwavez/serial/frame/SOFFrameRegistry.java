package com.rposcro.jwavez.serial.frame;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SOFFrameRegistry {

  private Map<Byte, Class<? extends SOFRequestFrame>> requestFramesMap;
  private Map<Byte, Class<? extends SOFCallbackFrame>> callbackFramesMap;
  private Map<Byte, Class<? extends SOFResponseFrame>> responseFramesMap;

  private static SOFFrameRegistry registryInstance;

  public static SOFFrameRegistry defaultRegistry() {
    if (registryInstance == null) {
      SOFFrameRegistry registry = new SOFFrameRegistry();
      registry.scanAndRegisterFrames("com.rposcro.zwave.serial.frame");
      registryInstance = registry;
    }
    return registryInstance;
  }

  public Optional<Class<? extends SOFRequestFrame>> requestClass(byte functionId) {
    return Optional.ofNullable(requestFramesMap.get(functionId));
  }

  public Optional<Class<? extends SOFRequestFrame>> requestClass(SerialCommand serialCommand) {
    return requestClass(serialCommand.getCode());
  }

  public Optional<Class<? extends SOFResponseFrame>> responseClass(byte functionId) {
    return Optional.ofNullable(responseFramesMap.get(functionId));
  }

  public Optional<Class<? extends SOFResponseFrame>> responseClass(SerialCommand serialCommand) {
    return responseClass(serialCommand.getCode());
  }

  public Optional<Class<? extends SOFCallbackFrame>> callbackClass(byte functionId) {
    return Optional.ofNullable(callbackFramesMap.get(functionId));
  }

  public Optional<Class<? extends SOFCallbackFrame>> callbackClass(SerialCommand serialCommand) {
    return callbackClass(serialCommand.getCode());
  }


  private void scanAndRegisterFrames(String basePackage) {
    try {
      Package scanPackage = SOFFrameRegistry.class.getPackage();
      List<Class> classList = findAllClasses(basePackage);

      this.responseFramesMap = new HashMap<>();
      this.requestFramesMap = new HashMap<>();
      this.callbackFramesMap = new HashMap<>();
      registerModels(classList);
      //validateRegistry();
    } catch(IOException e) {
      throw new SerialException("Failed to scan packages", e);
    }
  }

  private void validateRegistry() {
    if (Stream.of(SerialCommand.values()).anyMatch(this::incompleteModel)) {
      throw new IllegalStateException("Frame model incomplete!");
    }
  }

  private boolean incompleteModel(SerialCommand serialCommand) {
    if (!responseFramesMap.containsKey(serialCommand.getCode())
        || !requestFramesMap.containsKey(serialCommand.getCode())) {
      log.error("Frame model incomplete for serialCommand {}", serialCommand);
      return true;
    }
    return false;
  }

  private void registerModels(List<Class> classList) {
    classList.stream()
        .forEach(clazz -> {
          if (clazz.isAnnotationPresent(RequestFrameModel.class)) {
            registerRequestModel((Class<? extends SOFRequestFrame>) clazz);
          } else if (clazz.isAnnotationPresent(ResponseFrameModel.class)) {
            registerResponseModel((Class<? extends SOFResponseFrame>) clazz);
          } else if (clazz.isAnnotationPresent(CallbackFrameModel.class)) {
            registerCallbackModel((Class<? extends SOFCallbackFrame>) clazz);
          }
        });
  }

  private void registerRequestModel(Class<? extends SOFRequestFrame> modelClass) {
    if (!SOFRequestFrame.class.isAssignableFrom(modelClass)) {
      throw new IllegalStateException("Request frame model must be applied to SOFRequestFrame type only! Equipped class: " + modelClass);
    }
    RequestFrameModel modelAnnotation = (RequestFrameModel) modelClass.getAnnotation(RequestFrameModel.class);
    Byte functionId = modelAnnotation.function().getCode();
    if (requestFramesMap.containsKey(functionId)) {
      throw new IllegalStateException("Found ambiguous request model for " + modelAnnotation.function());
    }
    requestFramesMap.put(functionId, modelClass);
  }

  private void registerCallbackModel(Class<? extends SOFCallbackFrame> modelClass) {
    if (!SOFCallbackFrame.class.isAssignableFrom(modelClass)) {
      throw new IllegalStateException("Callback frame model must be applied to SOFCallbackFrame type only! Equipped class: " + modelClass);
    }
    CallbackFrameModel modelAnnotation = (CallbackFrameModel) modelClass.getAnnotation(CallbackFrameModel.class);
    Byte functionId = modelAnnotation.function().getCode();
    if (callbackFramesMap.containsKey(functionId)) {
      throw new IllegalStateException("Found ambiguous callback model for " + modelAnnotation.function());
    }
    callbackFramesMap.put(functionId, modelClass);
  }

  private void registerResponseModel(Class<? extends SOFResponseFrame> modelClass) {
    if (!SOFResponseFrame.class.isAssignableFrom(modelClass)) {
      throw new IllegalStateException("Response frame model must be applied to SOFResponseFrame type only! Equipped class: " + modelClass);
    }
    ResponseFrameModel modelAnnotation = (ResponseFrameModel) modelClass.getAnnotation(ResponseFrameModel.class);
    Byte functionId = modelAnnotation.function().getCode();
    if (responseFramesMap.containsKey(functionId)) {
      throw new IllegalStateException("Found ambiguous response model for " + modelAnnotation.function());
    }
    responseFramesMap.put(functionId, modelClass);
  }

  private List<Class> findAllClasses(String basePackage) throws IOException {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    assert classLoader != null;
    String path = basePackage.replace('.', '/');

    List<File> basePackageEntries = Collections.list(classLoader.getResources(path)).stream()
        .map(URL::getFile)
        .map(File::new)
        .collect(Collectors.toList());

    return basePackageEntries.stream()
        .map(entry -> scanPackageTreeForClasses(entry, basePackage))
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  private static List<Class<?>> scanPackageTreeForClasses(File packageFile, String packageName) {
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

    subPackages.stream()
        .forEach(subPackageFile -> classesList.addAll(
            scanPackageTreeForClasses(subPackageFile, packageName + '.' + subPackageFile.getName())));

    return classesList;
  }

  private static Class<?> loadClass(String packageName, File classFile) {
    try {
      return Class.forName(className(packageName, classFile));
    } catch(ClassNotFoundException e) {
      throw new SerialException("Failed to load class: " + packageName + '.' + classFile);
    }
  }

  private static String className(String packageName, File classFile) {
    return packageName + '.' + classFile.getName().substring(0, classFile.getName().length() - 6);
  }
}
