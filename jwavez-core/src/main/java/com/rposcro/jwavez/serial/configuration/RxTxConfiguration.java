package com.rposcro.jwavez.serial.configuration;

import com.rposcro.jwavez.serial.rxtx.InboundFrameProcessor;
import com.rposcro.jwavez.serial.rxtx.SerialCommunicationBroker;
import com.rposcro.jwavez.serial.rxtx.SerialInboundThread;
import com.rposcro.jwavez.serial.rxtx.SerialOutboundThread;
import com.rposcro.jwavez.serial.rxtx.SerialReceiver;
import com.rposcro.jwavez.serial.rxtx.SerialRouter;
import com.rposcro.jwavez.serial.rxtx.SerialTransmitter;
import gnu.io.NRSerialPort;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RxTxConfiguration {

  private SerialReceiver serialReceiver;
  private SerialTransmitter serialTransmitter;
  private SerialInboundThread serialInboundThread;
  private SerialOutboundThread serialOutboundThread;
  private SerialCommunicationBroker serialCommunicationBroker;
  private InboundFrameProcessor inboundFrameProcessor;
  private SerialRouter serialRouter;

  @Builder
  public RxTxConfiguration(NRSerialPort port, FramesConfiguration framesConfiguration) {
    this.serialCommunicationBroker = new SerialCommunicationBroker();
    this.serialReceiver = new SerialReceiver(port.getInputStream());
    this.serialTransmitter = new SerialTransmitter(port.getOutputStream());
    this.serialInboundThread = SerialInboundThread.builder()
        .serialReceiver(serialReceiver)
        .serialTransmitter(serialTransmitter)
        .build();
    this.serialOutboundThread = SerialOutboundThread.builder()
        .communicationBroker(serialCommunicationBroker)
        .build();
    this.inboundFrameProcessor = InboundFrameProcessor.builder()
        .communicationBroker(serialCommunicationBroker)
        .build();
    this.serialRouter = SerialRouter.builder()
        .communicationBroker(serialCommunicationBroker)
        .inboundThread(serialInboundThread)
        .outboundThread(serialOutboundThread)
        .transmitter(serialTransmitter)
        .frameParser(framesConfiguration.getFrameParser())
        .build();
  }

  public void activate() {
    this.serialOutboundThread.start();
    this.serialInboundThread.start();
    this.inboundFrameProcessor.start();
  }
}
