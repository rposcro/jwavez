package com.rposcro.jwavez.serial.utils.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class AsynchronousExecutors {

  private static ExecutorService defaultExecutorService = ForkJoinPool.commonPool();

  public static void registerDefaultExecutor(ExecutorService executorService) {
    defaultExecutorService = executorService;
  }

  public static ExecutorService defaultExecutor() {
    return defaultExecutorService;
  }
}
