package com.rposcro.jwavez.serial.controllers.inclusion;

import com.rposcro.jwavez.core.buffer.ByteBufferManager;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper;
import com.rposcro.jwavez.serial.frames.callbacks.RemoveNodeFromNetworkCallback;
import com.rposcro.jwavez.serial.frames.requests.RemoveNodeFromNetworkRequestBuilder;
import com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkMode;
import com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.ChecksumUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.ABORTING_OPERATION;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.CANCELLATION_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.CLEANING_UP_ERRORS;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.CONTROLLER_FOUND;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.FAILURE_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.NODE_FOUND;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.SLAVE_FOUND;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.TERMINATING_REMOVE_NODE;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.TERMINATION_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.WAITING_FOR_NODE;
import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.WAITING_FOR_PROTOCOL;
import static com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus.REMOVE_NODE_STATUS_DONE;
import static com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus.REMOVE_NODE_STATUS_FAILED;
import static com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus.REMOVE_NODE_STATUS_LEARN_READY;
import static com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus.REMOVE_NODE_STATUS_NODE_FOUND;
import static com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus.REMOVE_NODE_STATUS_REMOVING_CONTROLLER;
import static com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus.REMOVE_NODE_STATUS_REMOVING_SLAVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RemoveNodeFromNetworkFlowHandlerTest {

    private final static byte FLOW_ID = 0x44;

    @Mock
    private TransactionKeeper<RemoveNodeFromNetworkFlowState> transactionKeeper;

    private RemoveNodeFromNetworkFlowHandler flowHandler;

    @BeforeEach
    public void setUp() {
        flowHandler = new RemoveNodeFromNetworkFlowHandler(transactionKeeper, new RemoveNodeFromNetworkRequestBuilder(new ByteBufferManager()));
        flowHandler.startOver(FLOW_ID);
    }

    @ParameterizedTest
    @MethodSource("testArgumentsWithoutNextRequest")
    public void transitIsAsExpected(RemoveNodeFromNetworkFlowState currentState, RemoveNodeFromNeworkStatus callbackStatus, RemoveNodeFromNetworkFlowState expectedState) {
        KeeperState actualKeeperState = new KeeperState();
        RemoveNodeFromNetworkCallback callback = callback(callbackStatus);

        when(transactionKeeper.getState()).thenReturn(currentState);
        doAnswer((args) -> {
            actualKeeperState.flowState = args.getArgument(0);
            return null;
        }).when(transactionKeeper).transit(any(RemoveNodeFromNetworkFlowState.class));

        flowHandler.handleCallback(callback);

        assertEquals(expectedState, actualKeeperState.flowState);
    }

    @ParameterizedTest
    @MethodSource("testArgumentsWithNextRequest")
    public void transitIsAsExpected(RemoveNodeFromNetworkFlowState currentState, RemoveNodeFromNeworkStatus callbackStatus, RemoveNodeFromNetworkFlowState expectedState, int expectedRequestMode) {
        KeeperState actualKeeperState = new KeeperState();
        RemoveNodeFromNetworkCallback callback = callback(callbackStatus);

        when(transactionKeeper.getState()).thenReturn(currentState);
        doAnswer((args) -> {
            actualKeeperState.flowState = args.getArgument(0);
            actualKeeperState.nextRequestMode = args.getArgument(1, SerialRequest.class).getFrameData().getByte(4) & 0b00111111;
            return null;
        }).when(transactionKeeper).transitAndSchedule(any(RemoveNodeFromNetworkFlowState.class), any(SerialRequest.class));

        flowHandler.handleCallback(callback);

        assertEquals(expectedState, actualKeeperState.flowState);
        assertEquals(expectedRequestMode, actualKeeperState.nextRequestMode);
    }

    private static Stream<Arguments> testArgumentsWithoutNextRequest() {
        return Stream.of(
            Arguments.of(WAITING_FOR_PROTOCOL, REMOVE_NODE_STATUS_LEARN_READY, WAITING_FOR_NODE),
            Arguments.of(WAITING_FOR_NODE, REMOVE_NODE_STATUS_NODE_FOUND, NODE_FOUND),
            Arguments.of(NODE_FOUND, REMOVE_NODE_STATUS_REMOVING_SLAVE, SLAVE_FOUND),
            Arguments.of(NODE_FOUND, REMOVE_NODE_STATUS_REMOVING_CONTROLLER, CONTROLLER_FOUND),
            Arguments.of(ABORTING_OPERATION, REMOVE_NODE_STATUS_NODE_FOUND, NODE_FOUND)
        );
    }

    private static Stream<Arguments> testArgumentsWithNextRequest() {
        return Stream.of(
            Arguments.of(SLAVE_FOUND, REMOVE_NODE_STATUS_DONE, TERMINATING_REMOVE_NODE, RemoveNodeFromNeworkMode.REMOVE_NODE_STOP.getCode()),
            Arguments.of(SLAVE_FOUND, REMOVE_NODE_STATUS_FAILED, CLEANING_UP_ERRORS, RemoveNodeFromNeworkMode.REMOVE_NODE_STOP.getCode()),
            Arguments.of(CONTROLLER_FOUND, REMOVE_NODE_STATUS_DONE, TERMINATING_REMOVE_NODE, RemoveNodeFromNeworkMode.REMOVE_NODE_STOP.getCode()),
            Arguments.of(CONTROLLER_FOUND, REMOVE_NODE_STATUS_FAILED, CLEANING_UP_ERRORS, RemoveNodeFromNeworkMode.REMOVE_NODE_STOP.getCode()),
            Arguments.of(TERMINATING_REMOVE_NODE, REMOVE_NODE_STATUS_DONE, TERMINATION_STOP_SENT, RemoveNodeFromNeworkMode.REMOVE_NODE_STOP.getCode()),
            Arguments.of(CLEANING_UP_ERRORS, REMOVE_NODE_STATUS_DONE, FAILURE_STOP_SENT, RemoveNodeFromNeworkMode.REMOVE_NODE_STOP.getCode()),
            Arguments.of(ABORTING_OPERATION, REMOVE_NODE_STATUS_DONE, CANCELLATION_STOP_SENT, RemoveNodeFromNeworkMode.REMOVE_NODE_STOP.getCode())
        );
    }

    private RemoveNodeFromNetworkCallback callback(RemoveNodeFromNeworkStatus status) {
        byte[] data = new byte[] {0x06, 0x06, 0x00, 0x4a, FLOW_ID, status.getCode(), 0x00, 0x00};
        return callbackOfData(data);
    }

    private RemoveNodeFromNetworkCallback callbackOfData(byte[] data) {
        data[data.length - 1] = ChecksumUtil.frameCrc(data);
        ImmutableBuffer buffer = ImmutableBuffer.overBuffer(data);
        return new RemoveNodeFromNetworkCallback(buffer);
    }

    private class KeeperState {
        RemoveNodeFromNetworkFlowState flowState;
        int nextRequestMode;
    }
}
