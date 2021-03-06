package com.rposcro.jwavez.serial.probe;

import com.rposcro.jwavez.serial.probe.exceptions.CommunicationException;
import com.rposcro.jwavez.serial.probe.factory.FramesServicesFactory;
import com.rposcro.jwavez.serial.probe.factory.RoutingServicesFactory;
import com.rposcro.jwavez.serial.probe.factory.RxTxServicesFactory;
import com.rposcro.jwavez.serial.probe.factory.TransactionServicesFactory;
import com.rposcro.jwavez.serial.probe.frame.SOFFrame;
import com.rposcro.jwavez.serial.probe.frame.SOFFrameParser;
import com.rposcro.jwavez.serial.probe.frame.SOFFrameRegistry;
import com.rposcro.jwavez.serial.probe.frame.SOFFrameValidator;
import com.rposcro.jwavez.serial.probe.rxtx.InboundFrameInterceptor;
import com.rposcro.jwavez.serial.probe.rxtx.InboundFrameProcessor;
import com.rposcro.jwavez.serial.probe.rxtx.SerialCommunicationBroker;
import com.rposcro.jwavez.serial.probe.rxtx.SerialInboundTracker;
import com.rposcro.jwavez.serial.probe.rxtx.SerialOutboundTracker;
import com.rposcro.jwavez.serial.probe.rxtx.SerialReceiver;
import com.rposcro.jwavez.serial.probe.rxtx.SerialRouter;
import com.rposcro.jwavez.serial.probe.rxtx.SerialTransmitter;
import com.rposcro.jwavez.serial.probe.transactions.TransactionManager;
import gnu.io.NRSerialPort;
import java.util.List;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SerialChannelManager {

  private static final int DEFAULT_BAUD_RATE = 115200;

  @Getter private int baudRate;
  @Getter private String device;
  @Getter private boolean manageThreads;
  @Getter private List<InboundFrameInterceptor> interceptors;

  @Getter private SerialChannel serialChannel;
  @Getter private Runnable[] runnables;

  private NRSerialPort port;
  private Thread[] threads;

  private SerialReceiver serialReceiver;
  private SerialTransmitter serialTransmitter;
  private InboundFrameProcessor frameProcessor;

  public void addInboundFrameInterceptor(InboundFrameInterceptor interceptor) {
    frameProcessor.addInterceptor(interceptor);
  }

  public SerialChannel connect() {
    this.port = new NRSerialPort(device, baudRate);

    if (port.connect()) {
      assemblyChannel();

      try {
        cancelOngoingCommunication();
        if (manageThreads) {
          launchThreads();
        }
        return serialChannel;
      } catch(Exception e) {
        throw new CommunicationException("Failed to resetStreams communication with dongleDevice!", e);
      }
    } else {
      throw new CommunicationException("Failed to connect to dongleDevice!");
    }
  }

  public void disconnect() {
    this.port.disconnect();
  }

  private void cancelOngoingCommunication() throws Exception {
    serialTransmitter.transmitData(SOFFrame.CAN_FRAME.getBuffer());
    Thread.sleep(500);
    serialReceiver.purgeStream();
  }

  private void launchThreads() {
    threads = Stream.of(runnables)
        .map(Thread::new)
        .peek(Thread::start)
        .toArray(Thread[]::new);
  }

  @Builder
  private static SerialChannelManager build(
      String device,
      boolean manageThreads,
      int baudRate,
      @Singular("interceptor") List<InboundFrameInterceptor> interceptors) {
    SerialChannelManager manager = new SerialChannelManager();
    manager.device = device;
    manager.manageThreads = manageThreads;
    manager.baudRate = baudRate == 0 ? DEFAULT_BAUD_RATE : baudRate;
    manager.interceptors = interceptors;
    return manager;
  }

  private void assemblyChannel() {
    RxTxServicesFactory rxTxServicesFactory = new RxTxServicesFactory();
    this.serialReceiver = rxTxServicesFactory.createSerialReceiver(port.getInputStream());
    this.serialTransmitter = rxTxServicesFactory.createSerialTransmitter(port.getOutputStream());

    FramesServicesFactory framesServicesFactory = FramesServicesFactory.custom();
    SOFFrameParser frameParser = framesServicesFactory.createFrameParser();
    SOFFrameValidator frameValidator = framesServicesFactory.createFrameValidator();
    SOFFrameRegistry frameRegistry = framesServicesFactory.createFrameRegistry();

    RoutingServicesFactory routingServicesFactory = new RoutingServicesFactory();
    SerialCommunicationBroker communicationBroker = routingServicesFactory.createCommunicationBroker();
    SerialInboundTracker inboundTracker = routingServicesFactory.createInboundTracker(serialReceiver);
    SerialOutboundTracker outboundTracker = routingServicesFactory.createOutboundTracker(communicationBroker);
    SerialRouter serialRouter = routingServicesFactory.createSerialRouter(serialTransmitter, communicationBroker, frameParser, frameValidator);
    this.frameProcessor = routingServicesFactory.createFrameProcessor(communicationBroker);
    bindRouter(serialRouter, inboundTracker, outboundTracker);

    TransactionServicesFactory transactionServicesFactory = TransactionServicesFactory.custom();
    TransactionManager transactionManager = transactionServicesFactory.createTransactionManager(communicationBroker);

    frameProcessor.insertAsFirst(transactionManager);
    setUpInterceptors(frameProcessor, interceptors);

    this.runnables = new Runnable[] { inboundTracker, outboundTracker, frameProcessor };
    this.serialChannel = SerialChannel.builder()
        .inboundFrameProcessor(frameProcessor)
        .transactionManager(transactionManager)
        .frameRegistry(frameRegistry)
        .build();
  }

  private void setUpInterceptors(InboundFrameProcessor inboundFrameProcessor, List<InboundFrameInterceptor> interceptors) {
    interceptors.stream().forEach(inboundFrameProcessor::addInterceptor);
  }

  private NRSerialPort port() {
    return new NRSerialPort(device, baudRate);
  }

  private void bindRouter(SerialRouter router, SerialInboundTracker inboundTracker, SerialOutboundTracker outboundTracker) {
    inboundTracker.bindAckHandler(router::handleInboundACK);
    inboundTracker.bindCanHandler(router::handleInboundCAN);
    inboundTracker.bindNakHandler(router::handleInboundNAK);
    inboundTracker.bindSofHandler(router::handleInboundSOF);
    outboundTracker.bindOrderHandler(router::handleOutboundOrder);
  }
}
