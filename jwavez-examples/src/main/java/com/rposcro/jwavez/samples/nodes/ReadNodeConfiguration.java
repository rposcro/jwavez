package com.rposcro.jwavez.samples.nodes;

import com.rposcro.jwavez.core.commands.controlled.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.samples.AbstractExample;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadNodeConfiguration extends AbstractExample {

  private final NodeId addresseeId;
  private final GeneralAsynchronousController controller;

  public ReadNodeConfiguration(int nodeId) throws SerialPortException {
    this.addresseeId = new NodeId((byte) nodeId);
    String device = determineDevice();

    log.debug("Running for device " + device + " and node " + addresseeId.getId());

    InterceptableCallbackHandler callbacksHandler = new InterceptableCallbackHandler()
            .addViewBufferInterceptor(this::interceptViewBuffer);

    this.controller = GeneralAsynchronousController.builder()
            .callbackHandler(callbacksHandler)
            .dongleDevice(determineDevice())
            .build()
            .connect();
  }

  private void interceptViewBuffer(ViewBuffer buffer) {
    log.debug("Callback frame received: {}", BufferUtil.bufferToString(buffer));
  }

  public void readConfig(int... parameters) {
    try {
      for (int parameter : parameters) {
        log.debug("Sending configuration read request for parameter " + parameter);
        SerialRequest request = SendDataRequest.createSendDataRequest(
                addresseeId,
                new ConfigurationCommandBuilder().buildGetParameterCommand(parameter),
                nextFlowId());
        SendDataCallback callback = controller.requestCallbackFlow(request);
        if (callback.getTransmitCompletionStatus() == TransmitCompletionStatus.TRANSMIT_COMPLETE_OK) {
          log.debug("Request delivered successfully");
        } else if (callback.getTransmitCompletionStatus() == TransmitCompletionStatus.TRANSMIT_COMPLETE_NO_ACK) {
          log.debug("Request theoretically delivered however no ack");
        } else {
          log.debug("Dongle failed to deliver data: " + callback.getTransmitCompletionStatus());
          return;
        }
        Thread.sleep(5000);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void close() throws SerialPortException {
    controller.close();
  }

  public static void main(String[] args) throws Exception {
    ReadNodeConfiguration readConfigApp = new ReadNodeConfiguration(2);
    readConfigApp.readConfig(20);
    readConfigApp.close();
//    System.out.println(System.getenv("JWAVEZ_DEVICE"));
  }
}
