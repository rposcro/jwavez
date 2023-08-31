package com.rposcro.jwavez.serial.controllers.inclusion;

import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.CANCELLATION_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.FAILURE_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.TERMINATION_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.WAITING_FOR_NODE;

import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.JwzSerialSupport;
import com.rposcro.jwavez.serial.SerialRequestFactory;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.requests.AddNodeToNetworkRequestBuilder;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * This controller is used to run inclusion process on a dongle device. Under regular conditions, inclusion process
 * should be treated as an atomic not incorruptible flow, which implies the controller should be closed after each
 * inclusion session. When another session is required, new controller instance should be created and used for the
 * purpose.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddNodeToNetworkController extends AbstractInclusionController<AddNodeToNetworkFlowState, AddNodeToNetworkController> {

    public Optional<NodeInfo> listenForNodeToAdd() throws FlowException {
        runTransaction("add");
        return Optional.ofNullable(((AddNodeToNetworkFlowHandler) flowHandler).getNodeInfo());
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
    protected void timeoutTransaction(AddNodeToNetworkFlowState state) {
        if (WAITING_FOR_NODE == state) {
            flowHandler.stopTransaction();
        } else {
            flowHandler.killTransaction();
        }
    }

    @Builder
    public static AddNodeToNetworkController build(
            @NonNull String dongleDevice,
            RxTxConfiguration rxTxConfiguration,
            ExecutorService executorService,
            SerialRequestFactory serialRequestFactory,
            long waitForTouchTimeout,
            long waitForProgressTimeout) {
        AddNodeToNetworkRequestBuilder requestBuilder = serialRequestFactory == null ?
                JwzSerialSupport.defaultSupport().serialRequestFactory().addNodeToNetworkRequestsBuilder()
                : serialRequestFactory.addNodeToNetworkRequestsBuilder();
        AddNodeToNetworkController controller = new AddNodeToNetworkController();
        controller.transactionKeeper = new TransactionKeeper<>(controller::transactionStateChanged);
        controller.flowHandler = new AddNodeToNetworkFlowHandler(controller.transactionKeeper, requestBuilder);

        InterceptableCallbackHandler callbackHandler = new InterceptableCallbackHandler();
        controller.helpWithBuild(dongleDevice, rxTxConfiguration, null, callbackHandler, executorService);

        controller.waitForTouchTimeout = waitForTouchTimeout;
        controller.waitForProgressTimeout = waitForProgressTimeout;

        callbackHandler.addCallbackInterceptor(controller.flowHandler::handleCallback);
        return controller;
    }

    public static void main(String... args) throws SerialException {
        try (
                AddNodeToNetworkController controller = AddNodeToNetworkController.builder()
                        .dongleDevice("/dev/tty.usbmodem1411")
                        .build()
        ) {
            controller.connect();
            Optional<NodeInfo> nodeInfoWrap = controller.listenForNodeToAdd();
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
