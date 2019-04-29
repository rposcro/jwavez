package com.rposcro.jwavez.tools.cli.utils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class EasySemaphore {

  private Semaphore semaphore;

  public EasySemaphore() {
    this.semaphore = new Semaphore(1);
  }

  public boolean tryAcquire(long timeout, TimeUnit timeUnit) {
    try {
      return semaphore.tryAcquire(timeout, timeUnit);
    } catch(InterruptedException e) {
      return false;
    }
  }

  public void acquireUninterruptibly() {
    semaphore.acquireUninterruptibly();
  }

  public void release() {
    semaphore.release();
  }
}
