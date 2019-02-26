package com.rposcro.jwavez.serial.controllers.helpers;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;
import com.rposcro.jwavez.serial.rxtx.port.NeuronRoboticsSerialPort;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.NonNull;

public class ControllerConfiguration {

  @NonNull private String device;

  @Builder.Default private RxTxConfiguration rxTxConfiguration = RxTxConfiguration.builder().build();
  @Builder.Default private SerialPort serialPort = new NeuronRoboticsSerialPort();

  private Consumer<ViewBuffer> responseHandler;
  private Consumer<ViewBuffer> callbackHandler;
}
