package com.rposcro.jwavez.serial.controllers.inclusion;

import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.CANCELLATION_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.FAILURE_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.TERMINATION_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.WAITING_FOR_NODE;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.controllers.helpers.CallbackFlowIdDispatcher;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;
import com.rposcro.jwavez.serial.rxtx.RxTxRouterProcess;
import com.rposcro.jwavez.serial.rxtx.port.NeuronRoboticsSerialPort;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddNodeToNetworkController extends AbstractInclusionController<AddNodeToNetworkFlowState> {

  public Optional<NodeInfo> listenForNodeToAdd() throws FlowException {
    return runTransaction("add");
  }

  @Override
  protected boolean isFinalState(AddNodeToNetworkFlowState state) {
    return state == CANCELLATION_STOP_SENT || state == TERMINATION_STOP_SENT || state == FAILURE_STOP_SENT;
  }

  @Override
  protected boolean isWaitingForTouchState(AddNodeToNetworkFlowState state) {
    return WAITING_FOR_NODE == state;
  }

  @Override
  protected void finalizeTransaction(AddNodeToNetworkFlowState state) {
    switch(state) {
      case CANCELLATION_STOP_SENT:
        transactionKeeper.cancel();
        break;
      case FAILURE_STOP_SENT:
        transactionKeeper.fail();
        break;
      default:
        transactionKeeper.complete();
    }
  }

  @Override
  protected void timeoutTransaction(AddNodeToNetworkFlowState state) {
    if (WAITING_FOR_NODE == state) {
      flowHandler.stopTransaction();
    } else {
      flowHandler.killTransaction();
    }
  }

  @Builder
  public static AddNodeToNetworkController build(
      long waitForTouchTimeout,
      long waitForProgressTimeout,
      @NonNull InterceptableCallbackHandler callbackHandler,
      @NonNull CallbackFlowIdDispatcher flowIdDispatcher,
      @NonNull RxTxRouterProcess rxTxRouterProcess) {
    AddNodeToNetworkController controller = new AddNodeToNetworkController();
    controller.transactionKeeper = new TransactionKeeper<>(controller::transactionStateChanged);
    controller.flowHandler = new AddNodeToNetworkFlowHandler(controller.transactionKeeper);

    controller.flowIdDispatcher = flowIdDispatcher;
    controller.rxTxRouterProcess = rxTxRouterProcess;
    controller.waitForTouchTimeout = waitForTouchTimeout;
    controller.waitForProgressTimeout = waitForProgressTimeout;

    callbackHandler.addCallbackInterceptor(controller.flowHandler::handleCallback);
    return controller;
  }

  public static void main(String... args) throws SerialException {
    SerialPort serialPort = new NeuronRoboticsSerialPort();

    try {
      InterceptableCallbackHandler callbackHandler = new InterceptableCallbackHandler();
      RxTxRouterProcess rxTxRouterProcess = RxTxRouterProcess.builder()
          .callbackHandler(callbackHandler)
          .configuration(RxTxConfiguration.defaultConfiguration())
          .serialPort(serialPort)
          .build();
      AddNodeToNetworkController controller = AddNodeToNetworkController.builder()
          .callbackHandler(callbackHandler)
          .rxTxRouterProcess(rxTxRouterProcess)
          .flowIdDispatcher(new CallbackFlowIdDispatcher())
          //.waitForNodeTimeout(5_000)
          .build();

      serialPort.connect("/dev/tty.usbmodem1421");
      rxTxRouterProcess.initialize();
      Thread thread = new Thread(rxTxRouterProcess);
      thread.setDaemon(true);
      thread.start();

      Optional<NodeInfo> nodeInfoWrap = controller.listenForNodeToAdd();
      System.out.println(nodeInfoWrap.map(
          info -> String.format("id: %s\nbdc: %s\ngdc: %s\nsdc: %s",
              info.getId().getId(),
              info.getBasicDeviceClass(),
              info.getGenericDeviceClass(),
              info.getSpecificDeviceClass())
          ).orElse("No node found")
      );
    } finally {
      serialPort.disconnect();
    }
  }
}
