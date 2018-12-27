package com.rposcro.jwavez.serial.factory;

import com.rposcro.jwavez.serial.frame.SOFFrameParser;
import com.rposcro.jwavez.serial.frame.SOFFrameRegistry;
import com.rposcro.jwavez.serial.frame.SOFFrameValidator;
import java.util.concurrent.Semaphore;

public class FramesServicesFactory {

  private SOFFrameParser frameParser;
  private SOFFrameRegistry frameRegistry;
  private SOFFrameValidator frameValidator;

  private static final Semaphore semaphore = new Semaphore(1);
  private static FramesServicesFactory singleton;

  private FramesServicesFactory() {
    this.frameValidator = new SOFFrameValidator();
    this.frameRegistry = SOFFrameRegistry.defaultRegistry();
    this.frameParser = new SOFFrameParser(frameRegistry);
  }

  public SOFFrameParser createFrameParser() {
    return frameParser;
  }

  public SOFFrameRegistry createFrameRegistry() {
    return frameRegistry;
  }

  public SOFFrameValidator createFrameValidator() {
    return frameValidator;
  }

  public static FramesServicesFactory custom() {
    semaphore.acquireUninterruptibly();
    try {
      if (singleton == null) {
        singleton = new FramesServicesFactory();
      }
      return singleton;
    } finally {
      semaphore.release();
    }
  }
}
