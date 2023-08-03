package com.rposcro.jwavez.serial.controllers.inclusion

import com.rposcro.jwavez.serial.buffers.ViewBuffer
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper
import com.rposcro.jwavez.serial.frames.callbacks.AddNodeToNetworkCallback
import com.rposcro.jwavez.serial.model.AddNodeToNeworkMode
import com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus
import com.rposcro.jwavez.serial.utils.ChecksumUtil
import com.rposcro.jwavez.serial.utils.FramesUtil
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer

import static com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState.*
import static com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus.*

class AddNodeToNetworkFlowHandlerSpec extends Specification {

    AddNodeToNetworkFlowHandler handler;
    TransactionKeeper<AddNodeToNetworkFlowState> transactionKeeper;
    byte flowId;

    def setup() {
        def consumer = { arg -> };
        transactionKeeper = Mock(TransactionKeeper.class, constructorArgs: [consumer]);
        flowId = (byte) 0xdd;
        handler = new AddNodeToNetworkFlowHandler(transactionKeeper);
        handler.callbackFlowId = flowId;
    }

    @Unroll
    def "state transition where currentState #currentState, callbackStatus #callbackStatus"() {
        given:
        def actualState;
        def actualRequestMode;
        transactionKeeper.transit(_) >> { args ->
            actualState = args[0];
            actualRequestMode = 0;
        };
        transactionKeeper.transitAndSchedule(_, _) >> { args ->
            actualState = args[0];
            actualRequestMode = args[1].frameData.asByteBuffer().get(4) & 0b00111111;
        };
        transactionKeeper.getState() >> currentState;
        def callback = callback(callbackStatus);

        when:
        handler.handleCallback(callback);

        then:
        actualState == newState;
        actualRequestMode == requestMode;

        where:
        currentState         | callbackStatus                    | newState               | requestMode
        WAITING_FOR_PROTOCOL | ADD_NODE_STATUS_LEARN_READY       | WAITING_FOR_NODE       | 0
        WAITING_FOR_NODE     | ADD_NODE_STATUS_NODE_FOUND        | NODE_FOUND             | 0
        NODE_FOUND           | ADD_NODE_STATUS_ADDING_SLAVE      | SLAVE_FOUND            | 0
        NODE_FOUND           | ADD_NODE_STATUS_ADDING_CONTROLLER | CONTROLLER_FOUND       | 0
        SLAVE_FOUND          | ADD_NODE_STATUS_PROTOCOL_DONE     | TERMINATING_ADD_NODE   | AddNodeToNeworkMode.ADD_NODE_STOP.getCode()
        SLAVE_FOUND          | ADD_NODE_STATUS_FAILED            | CLEANING_UP_ERRORS     | AddNodeToNeworkMode.ADD_NODE_STOP.getCode()
        CONTROLLER_FOUND     | ADD_NODE_STATUS_PROTOCOL_DONE     | TERMINATING_ADD_NODE   | AddNodeToNeworkMode.ADD_NODE_STOP.getCode()
        CONTROLLER_FOUND     | ADD_NODE_STATUS_FAILED            | CLEANING_UP_ERRORS     | AddNodeToNeworkMode.ADD_NODE_STOP.getCode()
        TERMINATING_ADD_NODE | ADD_NODE_STATUS_DONE              | TERMINATION_STOP_SENT  | AddNodeToNeworkMode.ADD_NODE_STOP.getCode()
        CLEANING_UP_ERRORS   | ADD_NODE_STATUS_DONE              | FAILURE_STOP_SENT      | AddNodeToNeworkMode.ADD_NODE_STOP.getCode()
        ABORTING_OPERATION   | ADD_NODE_STATUS_DONE              | CANCELLATION_STOP_SENT | AddNodeToNeworkMode.ADD_NODE_STOP.getCode()
        ABORTING_OPERATION   | ADD_NODE_STATUS_NODE_FOUND        | NODE_FOUND             | 0
    }

    def callback(AddNodeToNeworkStatus status) {
        byte[] data = [0x06, 0x06, 0x00, 0x4a, flowId, status.getCode(), 0x00, 0x00];
        return callbackOfData(data);
    }

    def callbackOfData(byte[] data) {
        data[data.length - 1] = ChecksumUtil.frameCrc(data);
        ViewBuffer buffer = new ViewBuffer(ByteBuffer.wrap(data));
        buffer.setViewRange(0, data.length);
        return new AddNodeToNetworkCallback(buffer);
    }
}
