package com.rposcro.jwavez.serial.frames;

import com.rposcro.jwavez.core.exceptions.CodeIntegrityException;
import com.rposcro.jwavez.core.utils.PackageScanner;
import com.rposcro.jwavez.serial.frames.callbacks.Callback;
import com.rposcro.jwavez.serial.frames.responses.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Semaphore;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FramesModelRegistry {

  private Map<Byte, Class<? extends Callback>> callbackFramesMap;
  private Map<Byte, Class<? extends Response>> responseFramesMap;

  private static final Semaphore instanceSemaphore = new Semaphore(1);
  private static FramesModelRegistry registryInstance;

  private FramesModelRegistry() {
    this.responseFramesMap = new HashMap<>();
    this.callbackFramesMap = new HashMap<>();
  }

  public static FramesModelRegistry defaultRegistry() {
    instanceSemaphore.acquireUninterruptibly();
    try {
      if (registryInstance == null) {
        FramesModelRegistry registry = new FramesModelRegistry();
        registry.scanAndRegisterFrames("com.rposcro.jwavez.serial.frames");
        registryInstance = registry;
      }
      return registryInstance;
    } finally {
      instanceSemaphore.release();
    }
  }

  public Optional<Class<? extends Response>> responseClass(byte functionId) {
    return Optional.ofNullable(responseFramesMap.get(functionId));
  }

  public Optional<Class<? extends Callback>> callbackClass(byte functionId) {
    return Optional.ofNullable(callbackFramesMap.get(functionId));
  }

  private void scanAndRegisterFrames(String... basePackages) {
    log.debug("Scanning SOF frame classes");
    PackageScanner scanner = new PackageScanner();

    Set<Class<? extends Response>> responseClassList = scanner.findAllClassesOfType(Response.class, basePackages);
    responseClassList.stream()
        .filter(clazz -> clazz.isAnnotationPresent(ResponseFrameModel.class))
        .forEach(clazz -> registerResponseModel(clazz.asSubclass(Response.class)));

    Set<Class<? extends Callback>> callbackClassList = scanner.findAllClassesOfType(Callback.class, basePackages);
    callbackClassList.stream()
        .filter(clazz -> clazz.isAnnotationPresent(CallbackFrameModel.class))
        .forEach(clazz -> registerCallbackModel(clazz.asSubclass(Callback.class)));

    log.debug("Class registry: {} response classes, {} callback classes", responseFramesMap.size(), callbackFramesMap.size());
  }

  private void registerResponseModel(Class<? extends Response> modelClass) {
    ResponseFrameModel modelAnnotation = modelClass.getAnnotation(ResponseFrameModel.class);
    Byte functionId = modelAnnotation.function().getCode();
    if (responseFramesMap.containsKey(functionId)) {
      throw new CodeIntegrityException("Found ambiguous response model for " + modelAnnotation.function());
    }
    responseFramesMap.put(functionId, modelClass);
  }

  private void registerCallbackModel(Class<? extends Callback> modelClass) {
    CallbackFrameModel modelAnnotation = modelClass.getAnnotation(CallbackFrameModel.class);
    Byte functionId = modelAnnotation.function().getCode();
    if (callbackFramesMap.containsKey(functionId)) {
      throw new CodeIntegrityException("Found ambiguous callback model for " + modelAnnotation.function());
    }
    callbackFramesMap.put(functionId, modelClass);
  }
}
