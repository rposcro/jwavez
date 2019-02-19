package com.rposcro.jwavez.serial.controllers;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.responses.ZWaveResponse;
import com.rposcro.jwavez.serial.handlers.LastResponseHolder;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;
import com.rposcro.jwavez.serial.rxtx.RxTxRouter;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.rxtx.port.NeuronRoboticsSerialPort;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneralAsynchronousController implements AutoCloseable {

  private String device;
  private RxTxRouter rxTxRouter;
  private SerialPort serialPort;
  private ExecutorService executorService;
  private boolean selfExecutor;

  public GeneralAsynchronousController connect() throws SerialPortException {
    this.serialPort.connect(device);
    executorService.execute(this.rxTxRouter);
    return this;
  }

  @Override
  public void close() throws SerialPortException {
    this.rxTxRouter.stop();
    if (selfExecutor) {
      this.executorService.shutdownNow();
    }
    this.serialPort.disconnect();
  }

  @Builder
  private static GeneralAsynchronousController build(
      @NonNull String device,
      RxTxConfiguration configuration,
      ExecutorService executorService,
      Consumer<ViewBuffer> responseHandler,
      Consumer<ViewBuffer> callbackHandler) {
    GeneralAsynchronousController instance = new GeneralAsynchronousController();
    instance.device = device;
    instance.serialPort = new NeuronRoboticsSerialPort();
    instance.rxTxRouter = RxTxRouter.builder()
        .configuration(configuration != null ? configuration : RxTxConfiguration.builder().build())
        .serialPort(instance.serialPort)
        .responseHandler(responseHandler)
        .callbackHandler(callbackHandler)
        .build();

    instance.executorService = executorService == null ? Executors.newSingleThreadExecutor(GeneralAsynchronousController::makeThread) : executorService;
    instance.selfExecutor = executorService == null;

    return instance;
  }

  private static Thread makeThread(Runnable runnable) {
    Thread thread = new Thread(runnable);
    thread.setName(GeneralAsynchronousController.class + ".RxTxRouterThread");
    thread.setDaemon(true);
    return thread;
  }
}
