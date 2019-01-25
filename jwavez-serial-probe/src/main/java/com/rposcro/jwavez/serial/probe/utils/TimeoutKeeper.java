package com.rposcro.jwavez.serial.probe.utils;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeoutKeeper {

  private static final int POOL_SIZE = 20;
  private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(POOL_SIZE);

  static {
    executor.setRemoveOnCancelPolicy(true);
    executor.setThreadFactory(TimeoutKeeper::createThread);
  }

  private long timeoutMilliseconds;
  private TimeoutHandler handler;
  private ScheduledFuture future;

  public TimeoutKeeper(long timeoutMillis, TimeoutHandler handler) {
    this.timeoutMilliseconds = timeoutMillis;
    this.handler = handler;
  }

  public static TimeoutKeeper setTimeout(long timeoutMillis, TimeoutHandler handler) {
    TimeoutKeeper keeper = new TimeoutKeeper(timeoutMillis, handler);
    keeper.start();
    return keeper;
  }

  public static TimeoutKeeper setTimeout(long timeoutMillis) {
    TimeoutKeeper keeper = new TimeoutKeeper(timeoutMillis, () -> { throw new TimeoutException(); });
    keeper.start();
    return keeper;
  }

  public void park() throws TimeoutException {
    try {
      if (!(future.isCancelled() || future.isDone())) {
        this.future.get();
      }
    } catch (TimeoutException e) {
        throw e;
    } catch (Exception e) {
      log.debug("Keeper stopped {}", e.getClass());
    }
  }

  public void start() {
    this.future = executor.schedule(this::handle, timeoutMilliseconds, TimeUnit.MILLISECONDS);
  }

  public void cancel() {
    this.future.cancel(true);
  }

  private Void handle() {
    this.handler.handleTimeout();
    return null;
  }

  private static Thread createThread(Runnable runnable) {
    Thread thread = new Thread(runnable);
    thread.setDaemon(true);
    return thread;
  }
}
