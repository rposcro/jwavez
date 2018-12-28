package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.serial.frame.SOFFrame;
import java.util.concurrent.LinkedBlockingQueue;

public class SerialCommunicationBroker {

  private LinkedBlockingQueue<SOFFrame> inboundFrameQueue;
  private LinkedBlockingQueue<OutboundOrder> outboundOrderQueue;
  private LinkedBlockingQueue<OutboundResult> outboundResultQueue;

  public SerialCommunicationBroker() {
    inboundFrameQueue = new LinkedBlockingQueue<>();
    outboundOrderQueue = new LinkedBlockingQueue<>();
    outboundResultQueue = new LinkedBlockingQueue<>();
  }

  public void enqueueOutboundOrder(OutboundOrder outboundOrder) {
    outboundOrderQueue.add(outboundOrder);
  }

  OutboundOrder takeOutboundOrder() throws InterruptedException {
    return outboundOrderQueue.take();
  }

  void enqueueOutboundResult(OutboundResult outboundResult) {
    outboundResultQueue.add(outboundResult);
  }

  public OutboundResult takeOutboundResult() throws InterruptedException {
    return outboundResultQueue.take();
  }

  void enqueueInboundFrame(SOFFrame inboundFrame) {
    inboundFrameQueue.add(inboundFrame);
  }

  public SOFFrame takeInboundFrame() throws InterruptedException {
    return inboundFrameQueue.take();
  }
}
