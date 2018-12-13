package com.rposcro.jwavez.serial.rxtx;

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SerialOutboundThread extends Thread {

  private SerialCommunicationBroker communicationBroker;
  private Consumer<OutboundOrder> orderHandler = this::handleOrder;

  @Builder
  public SerialOutboundThread(SerialCommunicationBroker communicationBroker) {
    this.communicationBroker = communicationBroker;
  }

  public void bindOrderHandler(Consumer<OutboundOrder> orderHandler) {
    this.orderHandler = orderHandler;
  }

  @Override
  public void start() {
    setDaemon(false);
    super.start();
  }

  public void run() {
    while (!isInterrupted()) {
      try {
        OutboundOrder outboundOrder = communicationBroker.takeOutboundOrder();
        orderHandler.accept(outboundOrder);
      } catch(Exception e) {
        log.error("Failed sending outbound frame!", e);
      }
    }
  }

  private void handleOrder(OutboundOrder outboundOrder) {
    log.info("Default outbound order handler, dropping it");
  }
}
