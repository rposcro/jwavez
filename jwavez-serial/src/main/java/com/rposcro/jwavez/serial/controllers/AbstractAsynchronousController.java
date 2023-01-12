package com.rposcro.jwavez.serial.controllers;

import static com.rposcro.jwavez.core.utils.ObjectsUtil.orDefault;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.controllers.helpers.CallbackFlowIdDispatcher;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;
import com.rposcro.jwavez.serial.rxtx.RxTxRouterProcess;
import com.rposcro.jwavez.serial.rxtx.port.NeuronRoboticsSerialPort;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.rposcro.jwavez.serial.utils.FrameUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractAsynchronousController<T extends AbstractAsynchronousController> extends AbstractClosableController<T> {

  protected RxTxRouterProcess rxTxRouterProcess;
  protected CallbackFlowIdDispatcher callbackFlowIdDispatcher;
  protected ExecutorService executorService;

  protected boolean selfExecutor;

  @Override
  public T connect() throws SerialPortException {
    super.connect();
    executorService.execute(rxTxRouterProcess);
    return (T) this;
  }

  @Override
  public void close() throws SerialPortException {
    try {
      rxTxRouterProcess.stop();
      if (selfExecutor) {
        executorService.shutdown();
        try {
          if (!executorService.awaitTermination(rxTxConfiguration.getRouterPollDelay() * 4, TimeUnit.MILLISECONDS)) {
            executorService.shutdownNow();
          }
        } catch(InterruptedException e) {
          throw new SerialPortException("Interrupted while detaching from  serial port!", e);
        }
      }
    } finally {
      super.close();
    }
  }

  protected void helpWithBuild(
      @NonNull String dongleDevice,
      RxTxConfiguration rxTxConfiguration,
      Consumer<ViewBuffer> responseHandler,
      Consumer<ViewBuffer> callbackHandler,
      ExecutorService executorService) {
    this.dongleDevice = dongleDevice;
    this.serialPort = new NeuronRoboticsSerialPort();
    this.rxTxConfiguration = orDefault(rxTxConfiguration, RxTxConfiguration::defaultConfiguration);

    this.executorService = executorService == null ? Executors.newSingleThreadExecutor(this::makeThread) : executorService;
    this.selfExecutor = executorService == null;
    this.callbackFlowIdDispatcher = CallbackFlowIdDispatcher.shared();
    this.rxTxRouterProcess = RxTxRouterProcess.builder()
        .configuration(this.rxTxConfiguration)
        .serialPort(this.serialPort)
        .responseHandler(orDefault(responseHandler, this::handleResponse))
        .callbackHandler(orDefault(callbackHandler, this::handleCallback))
        .build();
  }

  private Thread makeThread(Runnable runnable) {
    Thread thread = new Thread(runnable);
    thread.setName(GeneralAsynchronousController.class.getSimpleName() + ".RxTxRouterThread");
    thread.setDaemon(true);
    return thread;
  }

  private void handleResponse(ViewBuffer frameBuffer) {
    if (log.isDebugEnabled()) {
      log.debug("Response frame received: {}", BufferUtil.bufferToString(frameBuffer));
      log.debug(FrameUtil.asFineString(frameBuffer));
    }
  }

  private void handleCallback(ViewBuffer frameBuffer) {
    if (log.isDebugEnabled()) {
      log.debug("Callback frame received: {}", BufferUtil.bufferToString(frameBuffer));
      log.debug(FrameUtil.asFineString(frameBuffer));
    }
  }
}
