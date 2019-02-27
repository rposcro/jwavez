package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.interceptors.CallbackInterceptor;
import com.rposcro.jwavez.serial.interceptors.ViewBufferInterceptor;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.options.AbstractDeviceBasedOptions;

public abstract class AbstractAsyncBasedCommand extends AbstractCommand {

  private InterceptableCallbackHandler callbackHandler;
  protected GeneralAsynchronousController controller;

  protected AbstractAsyncBasedCommand connect(AbstractDeviceBasedOptions options) throws CommandExecutionException {
    try {
      this.controller = GeneralAsynchronousController.builder()
          .device(options.getDevice())
          .callbackHandler(this.callbackHandler = new InterceptableCallbackHandler())
          .build()
          .connect();
      return this;
    } catch(SerialPortException e) {
      throw new CommandExecutionException("Failed to open serial port", e);
    }
  }

  public AbstractAsyncBasedCommand addCallbackInterceptor(CallbackInterceptor interceptor) {
    callbackHandler.addCallbackInterceptor(interceptor);
    return this;
  }

  public AbstractAsyncBasedCommand addCallbackInterceptor(ViewBufferInterceptor interceptor) {
    callbackHandler.addViewbufferInterceptor(interceptor);
    return this;
  }

  @Override
  public void close() throws SerialPortException {
    controller.close();
  }
}
