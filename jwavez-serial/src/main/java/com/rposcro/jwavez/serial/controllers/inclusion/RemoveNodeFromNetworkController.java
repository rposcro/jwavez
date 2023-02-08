package com.rposcro.jwavez.serial.controllers.inclusion;

import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.CANCELLATION_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.FAILURE_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.TERMINATING_REMOVE_NODE;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.TERMINATION_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.WAITING_FOR_NODE;

import com.rposcro.jwavez.core.model.NodeInfo;
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
public class RemoveNodeFromNetworkController extends AbstractInclusionController<RemoveNodeFromNetworkFlowState, RemoveNodeFromNetworkController> {

    public Optional<NodeInfo> listenForNodeToRemove() throws FlowException {
        runTransaction("remove");
        return Optional.ofNullable(((RemoveNodeFromNetworkFlowHandler) flowHandler).getNodeInfo());
    }

    @Override
    protected boolean isFinalState(RemoveNodeFromNetworkFlowState state) {
        return state == CANCELLATION_STOP_SENT || state == TERMINATION_STOP_SENT || state == FAILURE_STOP_SENT;
    }

    @Override
    protected boolean isWaitingForTouchState(RemoveNodeFromNetworkFlowState state) {
        return WAITING_FOR_NODE == state;
    }

    @Override
    protected void finalizeTransaction(RemoveNodeFromNetworkFlowState state) {
        switch (state) {
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
    protected void timeoutTransaction(RemoveNodeFromNetworkFlowState state) {
        if (WAITING_FOR_NODE == state) {
            flowHandler.stopTransaction();
        } else if (TERMINATING_REMOVE_NODE == state) {
            log.info("No callback to the first stopping frame, assuming dongle not to follow protocol");
            ((RemoveNodeFromNetworkFlowHandler) flowHandler).byPassTermination();
        } else {
            flowHandler.killTransaction();
        }
    }

    @Builder
    public static RemoveNodeFromNetworkController build(
            @NonNull String dongleDevice,
            RxTxConfiguration rxTxConfiguration,
            ExecutorService executorService,
            long waitForTouchTimeout,
            long waitForProgressTimeout) {
        RemoveNodeFromNetworkController controller = new RemoveNodeFromNetworkController();
        controller.transactionKeeper = new TransactionKeeper<>(controller::transactionStateChanged);
        controller.flowHandler = new RemoveNodeFromNetworkFlowHandler(controller.transactionKeeper);

        InterceptableCallbackHandler callbackHandler = new InterceptableCallbackHandler();
        controller.helpWithBuild(dongleDevice, rxTxConfiguration, null, callbackHandler, executorService);

        controller.waitForTouchTimeout = waitForTouchTimeout;
        controller.waitForProgressTimeout = waitForProgressTimeout;

        callbackHandler.addCallbackInterceptor(controller.flowHandler::handleCallback);
        return controller;
    }

    public static void main(String... args) throws SerialException {
        try (
                RemoveNodeFromNetworkController controller = RemoveNodeFromNetworkController.builder()
                        .dongleDevice("/dev/tty.usbmodem1411")
                        .build()
        ) {
            controller.connect();
            Optional<NodeInfo> nodeInfoWrap = controller.listenForNodeToRemove();
            System.out.println(nodeInfoWrap.map(
                            info -> String.format("id: %s\nbdc: %s\ngdc: %s\nsdc: %s",
                                    info.getId().getId(),
                                    info.getBasicDeviceClass(),
                                    info.getGenericDeviceClass(),
                                    info.getSpecificDeviceClass())
                    ).orElse("No node found")
            );
        }
    }
}
