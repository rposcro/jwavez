package com.rposcro.jwavez.serial.probe.factory;

import com.rposcro.jwavez.serial.probe.frame.SOFFrameParser;
import com.rposcro.jwavez.serial.probe.frame.SOFFrameValidator;
import com.rposcro.jwavez.serial.probe.rxtx.InboundFrameProcessor;
import com.rposcro.jwavez.serial.probe.rxtx.SerialCommunicationBroker;
import com.rposcro.jwavez.serial.probe.rxtx.SerialInboundTracker;
import com.rposcro.jwavez.serial.probe.rxtx.SerialOutboundTracker;
import com.rposcro.jwavez.serial.probe.rxtx.SerialReceiver;
import com.rposcro.jwavez.serial.probe.rxtx.SerialRouter;
import com.rposcro.jwavez.serial.probe.rxtx.SerialTransmitter;

public class RoutingServicesFactory {

  public SerialInboundTracker createInboundTracker(SerialReceiver serialReceiver) {
    return SerialInboundTracker.builder()
        .serialReceiver(serialReceiver)
        .build();
  }

  public SerialOutboundTracker createOutboundTracker(SerialCommunicationBroker communicationBroker) {
    return SerialOutboundTracker.builder()
        .communicationBroker(communicationBroker)
        .build();
  }

  public InboundFrameProcessor createFrameProcessor(SerialCommunicationBroker communicationBroker) {
    return InboundFrameProcessor.builder()
        .communicationBroker(communicationBroker)
        .build();
  }

  public SerialCommunicationBroker createCommunicationBroker() {
    return new SerialCommunicationBroker();
  }

  public SerialRouter createSerialRouter(SerialTransmitter transmitter, SerialCommunicationBroker communicationBroker, SOFFrameParser frameParser, SOFFrameValidator frameValidator) {
    return SerialRouter.builder()
        .transmitter(transmitter)
        .communicationBroker(communicationBroker)
        .frameParser(frameParser)
        .frameValidator(frameValidator)
        .build();
  }
}
