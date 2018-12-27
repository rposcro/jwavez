package com.rposcro.jwavez.serial.rxtx;

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SerialOutboundTracker implements Runnable {

  private SerialCommunicationBroker communicationBroker;
  private Consumer<OutboundOrder> orderHandler = this::handleOrder;

  @Builder
  public SerialOutboundTracker(SerialCommunicationBroker communicationBroker) {
    this.communicationBroker = communicationBroker;
  }

  public void bindOrderHandler(Consumer<OutboundOrder> orderHandler) {
    this.orderHandler = orderHandler;
  }

  public void run() {
    try {
      OutboundOrder outboundOrder = communicationBroker.takeOutboundOrder();
      orderHandler.accept(outboundOrder);
    } catch(InterruptedException e) {
      log.error("Outbound tracker interrupted!", e);
    }
  }

  private void handleOrder(OutboundOrder outboundOrder) {
    log.info("Default outbound order handler, dropping it");
  }
}
