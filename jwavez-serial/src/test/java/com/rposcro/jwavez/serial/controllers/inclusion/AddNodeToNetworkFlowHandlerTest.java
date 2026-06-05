package com.rposcro.jwavez.serial.controllers.inclusion;

import com.rposcro.jwavez.core.buffer.ByteBufferManager;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper;
import com.rposcro.jwavez.serial.frames.callbacks.AddNodeToNetworkCallback;
import com.rposcro.jwavez.serial.frames.requests.AddNodeToNetworkRequestBuilder;
import com.rposcro.jwavez.serial.model.AddNodeToNeworkMode;
import com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus;
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

import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.ABORTING_OPERATION;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.CANCELLATION_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.CLEANING_UP_ERRORS;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.CONTROLLER_FOUND;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.FAILURE_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.NODE_FOUND;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.SLAVE_FOUND;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.TERMINATING_ADD_NODE;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.TERMINATION_STOP_SENT;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.WAITING_FOR_NODE;
import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.WAITING_FOR_PROTOCOL;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_ADDING_CONTROLLER;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_ADDING_SLAVE;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_DONE;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_FAILED;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_LEARN_READY;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_NODE_FOUND;
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.ADD_NODE_STATUS_PROTOCOL_DONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddNodeToNetworkFlowHandlerTest {

    private final static byte FLOW_ID = 0x44;

    @Mock
    private TransactionKeeper<AddNodeToNetworkFlowState> transactionKeeper;

    private AddNodeToNetworkFlowHandler flowHandler;

    @BeforeEach
    public void setUp() {
        flowHandler = new AddNodeToNetworkFlowHandler(transactionKeeper, new AddNodeToNetworkRequestBuilder(new ByteBufferManager()));
        flowHandler.startOver(FLOW_ID);
    }

    @ParameterizedTest
    @MethodSource("testArgumentsWithoutNextRequest")
    public void transitIsAsExpected(AddNodeToNetworkFlowState currentState, AddNodeToNeworkStatus callbackStatus, AddNodeToNetworkFlowState expectedState) {
        KeeperState actualKeeperState = new KeeperState();
        AddNodeToNetworkCallback callback = callback(callbackStatus);

        when(transactionKeeper.getState()).thenReturn(currentState);
        doAnswer((args) -> {
            actualKeeperState.flowState = args.getArgument(0);
            return null;
        }).when(transactionKeeper).transit(any(AddNodeToNetworkFlowState.class));

        flowHandler.handleCallback(callback);

        assertEquals(expectedState, actualKeeperState.flowState);
    }

    @ParameterizedTest
    @MethodSource("testArgumentsWithNextRequest")
    public void transitIsAsExpected(AddNodeToNetworkFlowState currentState, AddNodeToNeworkStatus callbackStatus, AddNodeToNetworkFlowState expectedState, int expectedRequestMode) {
        KeeperState actualKeeperState = new KeeperState();
        AddNodeToNetworkCallback callback = callback(callbackStatus);

        when(transactionKeeper.getState()).thenReturn(currentState);
        doAnswer((args) -> {
            actualKeeperState.flowState = args.getArgument(0);
            actualKeeperState.nextRequestMode = args.getArgument(1, SerialRequest.class).getFrameData().getByte(4) & 0b00111111;
            return null;
        }).when(transactionKeeper).transitAndSchedule(any(AddNodeToNetworkFlowState.class), any(SerialRequest.class));

        flowHandler.handleCallback(callback);

        assertEquals(expectedState, actualKeeperState.flowState);
        assertEquals(expectedRequestMode, actualKeeperState.nextRequestMode);
    }

    private static Stream<Arguments> testArgumentsWithoutNextRequest() {
        return Stream.of(
            Arguments.of(WAITING_FOR_PROTOCOL, ADD_NODE_STATUS_LEARN_READY, WAITING_FOR_NODE),
            Arguments.of(WAITING_FOR_NODE, ADD_NODE_STATUS_NODE_FOUND, NODE_FOUND),
            Arguments.of(NODE_FOUND, ADD_NODE_STATUS_ADDING_SLAVE, SLAVE_FOUND),
            Arguments.of(NODE_FOUND, ADD_NODE_STATUS_ADDING_CONTROLLER, CONTROLLER_FOUND),
            Arguments.of(ABORTING_OPERATION, ADD_NODE_STATUS_NODE_FOUND, NODE_FOUND)
        );
    }

    private static Stream<Arguments> testArgumentsWithNextRequest() {
        return Stream.of(
            Arguments.of(SLAVE_FOUND, ADD_NODE_STATUS_PROTOCOL_DONE, TERMINATING_ADD_NODE, AddNodeToNeworkMode.ADD_NODE_STOP.getCode()),
            Arguments.of(SLAVE_FOUND, ADD_NODE_STATUS_FAILED, CLEANING_UP_ERRORS, AddNodeToNeworkMode.ADD_NODE_STOP.getCode()),
            Arguments.of(CONTROLLER_FOUND, ADD_NODE_STATUS_PROTOCOL_DONE, TERMINATING_ADD_NODE, AddNodeToNeworkMode.ADD_NODE_STOP.getCode()),
            Arguments.of(CONTROLLER_FOUND, ADD_NODE_STATUS_FAILED, CLEANING_UP_ERRORS, AddNodeToNeworkMode.ADD_NODE_STOP.getCode()),
            Arguments.of(TERMINATING_ADD_NODE, ADD_NODE_STATUS_DONE, TERMINATION_STOP_SENT, AddNodeToNeworkMode.ADD_NODE_STOP.getCode()),
            Arguments.of(CLEANING_UP_ERRORS, ADD_NODE_STATUS_DONE, FAILURE_STOP_SENT, AddNodeToNeworkMode.ADD_NODE_STOP.getCode()),
            Arguments.of(ABORTING_OPERATION, ADD_NODE_STATUS_DONE, CANCELLATION_STOP_SENT, AddNodeToNeworkMode.ADD_NODE_STOP.getCode())
        );
    }

    private AddNodeToNetworkCallback callback(AddNodeToNeworkStatus status) {
        byte[] data = new byte[] {0x06, 0x06, 0x00, 0x4a, FLOW_ID, status.getCode(), 0x00, 0x00};
        return callbackOfData(data);
    }

    private AddNodeToNetworkCallback callbackOfData(byte[] data) {
        data[data.length - 1] = ChecksumUtil.frameCrc(data);
        ImmutableBuffer buffer = ImmutableBuffer.overBuffer(data);
        return new AddNodeToNetworkCallback(buffer);
    }

    private class KeeperState {
        AddNodeToNetworkFlowState flowState;
        int nextRequestMode;
    }
}
