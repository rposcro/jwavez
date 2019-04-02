package com.rposcro.jwavez.serial.utils.async;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AsyncTaskExecutor {

  public static final int DEFAULT_THREAD_POOL_SIZE  = 10;

  private ExecutorService executorService;

  public <T> T executeTask(AsyncTask<T> task, long timeout) throws SerialException {
    final CompletableFuture<T> futureResult = new CompletableFuture<>();
    return null;
  }

  public static AsyncTaskExecutor defaultSharedExecutor() {
    return new AsyncTaskExecutor(ForkJoinPool.commonPool());
  }
}
