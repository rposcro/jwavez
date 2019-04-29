package com.rposcro.jwavez.serial.frames;

import com.rposcro.jwavez.core.exceptions.CodeIntegrityException;
import com.rposcro.jwavez.core.utils.PackageScanner;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.frames.responses.ZWaveResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Semaphore;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FramesModelRegistry {

  private Map<Byte, Class<? extends ZWaveCallback>> callbackFramesMap;
  private Map<Byte, Class<? extends ZWaveResponse>> responseFramesMap;

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

  public Optional<Class<? extends ZWaveResponse>> responseClass(byte commandCode) {
    return Optional.ofNullable(responseFramesMap.get(commandCode));
  }

  public Optional<Class<? extends ZWaveCallback>> callbackClass(byte commandCode) {
    return Optional.ofNullable(callbackFramesMap.get(commandCode));
  }

  private void scanAndRegisterFrames(String... basePackages) {
    log.debug("Scanning SOF frame classes");
    PackageScanner scanner = new PackageScanner();

    Set<Class<? extends ZWaveResponse>> responseClassList = scanner.findAllClassesOfType(ZWaveResponse.class, basePackages);
    responseClassList.stream()
        .filter(clazz -> clazz.isAnnotationPresent(ResponseFrameModel.class))
        .forEach(clazz -> registerResponseModel(clazz.asSubclass(ZWaveResponse.class)));

    Set<Class<? extends ZWaveCallback>> callbackClassList = scanner.findAllClassesOfType(ZWaveCallback.class, basePackages);
    callbackClassList.stream()
        .filter(clazz -> clazz.isAnnotationPresent(CallbackFrameModel.class))
        .forEach(clazz -> registerCallbackModel(clazz.asSubclass(ZWaveCallback.class)));

    log.debug("Class registry: {} response classes, {} callback classes", responseFramesMap.size(), callbackFramesMap.size());
  }

  private void registerResponseModel(Class<? extends ZWaveResponse> modelClass) {
    ResponseFrameModel modelAnnotation = modelClass.getAnnotation(ResponseFrameModel.class);
    Byte commandCode = modelAnnotation.function().getCode();
    if (responseFramesMap.containsKey(commandCode)) {
      throw new CodeIntegrityException("Found ambiguous response model for " + modelAnnotation.function());
    }
    responseFramesMap.put(commandCode, modelClass);
  }

  private void registerCallbackModel(Class<? extends ZWaveCallback> modelClass) {
    CallbackFrameModel modelAnnotation = modelClass.getAnnotation(CallbackFrameModel.class);
    Byte functionId = modelAnnotation.function().getCode();
    if (callbackFramesMap.containsKey(functionId)) {
      throw new CodeIntegrityException("Found ambiguous callback model for " + modelAnnotation.function());
    }
    callbackFramesMap.put(functionId, modelClass);
  }
}
