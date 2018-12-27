package com.rposcro.jwavez.serial.factory;

import com.rposcro.jwavez.serial.frame.SOFFrameParser;
import com.rposcro.jwavez.serial.frame.SOFFrameRegistry;
import com.rposcro.jwavez.serial.frame.SOFFrameValidator;
import java.util.concurrent.Semaphore;

class FramesServicesFactory {

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

  SOFFrameParser createFrameParser() {
    return frameParser;
  }

  SOFFrameRegistry createFrameRegistry() {
    return frameRegistry;
  }

  SOFFrameValidator createFrameValidator() {
    return frameValidator;
  }

  static FramesServicesFactory custom() {
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
