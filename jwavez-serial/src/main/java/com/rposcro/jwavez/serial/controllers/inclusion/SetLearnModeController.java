package com.rposcro.jwavez.serial.controllers.inclusion;

import static com.rposcro.jwavez.serial.controllers.inclusion.SetLearnModeFlowState.LEARN_MODE_ACTIVATED;
import static com.rposcro.jwavez.serial.controllers.inclusion.SetLearnModeFlowState.LEARN_MODE_CANCELLED;
import static com.rposcro.jwavez.serial.controllers.inclusion.SetLearnModeFlowState.LEARN_MODE_DONE;
import static com.rposcro.jwavez.serial.controllers.inclusion.SetLearnModeFlowState.LEARN_MODE_FAILED;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SetLearnModeController extends AbstractInclusionController<SetLearnModeFlowState, SetLearnModeController> {

    public Optional<NodeId> activateLearnMode() throws FlowException {
        return runTransaction("learn");
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
        switch (state) {
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
            @NonNull String dongleDevice,
            RxTxConfiguration rxTxConfiguration,
            ExecutorService executorService,
            long waitForTouchTimeout,
            long waitForProgressTimeout) {
        SetLearnModeController controller = new SetLearnModeController();
        controller.transactionKeeper = new TransactionKeeper<>(controller::transactionStateChanged);
        controller.flowHandler = new SetLearnModeFlowHandler(controller.transactionKeeper);

        InterceptableCallbackHandler callbackHandler = new InterceptableCallbackHandler();
        controller.helpWithBuild(dongleDevice, rxTxConfiguration, null, callbackHandler, executorService);

        controller.waitForTouchTimeout = waitForTouchTimeout;
        controller.waitForProgressTimeout = waitForProgressTimeout;

        callbackHandler.addCallbackInterceptor(controller.flowHandler::handleCallback);
        return controller;
    }

    public static void main(String... args) throws SerialException {
        try (
                SetLearnModeController controller = SetLearnModeController.builder()
                        .dongleDevice("/dev/tty.usbmodem1411")
                        .build()
        ) {
            controller.connect();
            Optional<NodeId> nodeId = controller.activateLearnMode();
            if (nodeId.isPresent()) {
                System.out.println("Received node id: " + nodeId.get().getId());
            } else {
                System.out.println("No node id received");
            }
        }
    }
}
