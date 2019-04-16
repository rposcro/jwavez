package com.rposcro.jwavez.serial.controllers.inclusion;

import static com.rposcro.jwavez.serial.controllers.inclusion.SetLearnModeFlowState.LEARN_MODE_ACTIVATED;
import static com.rposcro.jwavez.serial.controllers.inclusion.SetLearnModeFlowState.LEARN_MODE_CANCELLED;
import static com.rposcro.jwavez.serial.controllers.inclusion.SetLearnModeFlowState.LEARN_MODE_DONE;
import static com.rposcro.jwavez.serial.controllers.inclusion.SetLearnModeFlowState.LEARN_MODE_FAILED;

import com.rposcro.jwavez.serial.controllers.helpers.CallbackFlowIdDispatcher;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;
import com.rposcro.jwavez.serial.rxtx.RxTxRouterProcess;
import com.rposcro.jwavez.serial.rxtx.port.NeuronRoboticsSerialPort;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SetLearnModeController extends AbstractInclusionController<SetLearnModeFlowState> {

  public boolean activateLearnMode() throws FlowException {
    runTransaction("learn");
    return transactionKeeper.isSuccessful();
  }

  @Override
  protected boolean isFinalState(SetLearnModeFlowState state) {
    return state == LEARN_MODE_DONE || state == LEARN_MODE_CANCELLED || state == LEARN_MODE_FAILED;
  }

  @Override
  protected boolean isWaitingForTouchState(SetLearnModeFlowState state) {
    return LEARN_MODE_ACTIVATED == state;
  }

  @Override
  protected void finalizeTransaction(SetLearnModeFlowState state) {
    switch(state) {
      case LEARN_MODE_CANCELLED:
        transactionKeeper.cancel();
        break;
      case LEARN_MODE_FAILED:
        transactionKeeper.fail();
        break;
      default:
        transactionKeeper.complete();
    }
  }

  @Override
  protected void timeoutTransaction(SetLearnModeFlowState state) {
    flowHandler.stopTransaction();
  }

  @Builder
  public static SetLearnModeController build(
      long waitForTouchTimeout,
      long waitForProgressTimeout,
      @NonNull InterceptableCallbackHandler callbackHandler,
      @NonNull CallbackFlowIdDispatcher flowIdDispatcher,
      @NonNull RxTxRouterProcess rxTxRouterProcess) {
    SetLearnModeController controller = new SetLearnModeController();
    controller.transactionKeeper = new TransactionKeeper<>(controller::transactionStateChanged);
    controller.flowHandler = new SetLearnModeFlowHandler(controller.transactionKeeper);

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
      SetLearnModeController controller = SetLearnModeController.builder()
          .callbackHandler(callbackHandler)
          .rxTxRouterProcess(rxTxRouterProcess)
          .flowIdDispatcher(new CallbackFlowIdDispatcher())
          //.waitForNodeTimeout(5_000)
          .build();

      serialPort.connect("/dev/tty.usbmodem14211");
      rxTxRouterProcess.initialize();
      Thread thread = new Thread(rxTxRouterProcess);
      thread.setDaemon(true);
      thread.start();

      boolean success = controller.activateLearnMode();
      System.out.println("Success: " + success);
    } finally {
      serialPort.disconnect();
    }
  }
}
