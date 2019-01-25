package com.rposcro.jwavez.serial.probe.frame;

import com.rposcro.jwavez.core.utils.PackageScanner;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Semaphore;
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

  private static final Semaphore instanceSemaphore = new Semaphore(1);
  private static SOFFrameRegistry registryInstance;

  public static SOFFrameRegistry defaultRegistry() {
    instanceSemaphore.acquireUninterruptibly();
    try {
      if (registryInstance == null) {
        SOFFrameRegistry registry = new SOFFrameRegistry();
        registry.scanAndRegisterFrames();
        registryInstance = registry;
      }
      return registryInstance;
    } finally {
      instanceSemaphore.release();
    }
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

  private void scanAndRegisterFrames() {
    log.debug("Scanning SOF frame classes");
    PackageScanner scanner = new PackageScanner();
    Set<Class<? extends SOFFrame>> classList = scanner.findAllClassesOfType(SOFFrame.class, "com.rposcro.jwavez.serial.probe.frame");
    this.responseFramesMap = new HashMap<>();
    this.requestFramesMap = new HashMap<>();
    this.callbackFramesMap = new HashMap<>();
    registerModels(classList);
    log.debug("Class registry: {} request classes, {} response classes, {} callback classes", requestFramesMap.size(), responseFramesMap.size(), callbackFramesMap.size());
    //validateRegistry();
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

  private void registerModels(Set<Class<? extends SOFFrame>> classList) {
    classList.stream()
        .forEach(clazz -> {
          if (clazz.isAnnotationPresent(RequestFrameModel.class)) {
            registerRequestModel(clazz.asSubclass(SOFRequestFrame.class));
          } else if (clazz.isAnnotationPresent(ResponseFrameModel.class)) {
            registerResponseModel(clazz.asSubclass(SOFResponseFrame.class));
          } else if (clazz.isAnnotationPresent(CallbackFrameModel.class)) {
            registerCallbackModel(clazz.asSubclass(SOFCallbackFrame.class));
          }
        });
  }

  private void registerRequestModel(Class<? extends SOFRequestFrame> modelClass) {
    if (!SOFRequestFrame.class.isAssignableFrom(modelClass)) {
      throw new IllegalStateException("Request frame model must be applied to SOFRequestFrame type only! Equipped class: " + modelClass);
    }
    RequestFrameModel modelAnnotation = modelClass.getAnnotation(RequestFrameModel.class);
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
    CallbackFrameModel modelAnnotation = modelClass.getAnnotation(CallbackFrameModel.class);
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
    ResponseFrameModel modelAnnotation = modelClass.getAnnotation(ResponseFrameModel.class);
    Byte functionId = modelAnnotation.function().getCode();
    if (responseFramesMap.containsKey(functionId)) {
      throw new IllegalStateException("Found ambiguous response model for " + modelAnnotation.function());
    }
    responseFramesMap.put(functionId, modelClass);
  }
}
