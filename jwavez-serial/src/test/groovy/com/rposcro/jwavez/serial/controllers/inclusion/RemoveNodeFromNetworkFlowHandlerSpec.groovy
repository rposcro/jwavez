package com.rposcro.jwavez.serial.controllers.inclusion

import com.rposcro.jwavez.serial.buffers.ViewBuffer
import com.rposcro.jwavez.serial.controllers.helpers.TransactionKeeper
import com.rposcro.jwavez.serial.frames.callbacks.RemoveNodeFromNetworkCallback
import com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkMode
import com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus
import com.rposcro.jwavez.serial.utils.FrameUtil
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer

import static com.rposcro.jwavez.serial.controllers.inclusion.RemoveNodeFromNetworkFlowState.*
import static com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus.*

class RemoveNodeFromNetworkFlowHandlerSpec extends Specification {

    RemoveNodeFromNetworkFlowHandler handler;
    TransactionKeeper<RemoveNodeFromNetworkFlowState> transactionKeeper;
    byte flowId;

    def setup() {
        def consumer = { arg -> };
        transactionKeeper = Mock(TransactionKeeper.class, constructorArgs: [ consumer ]);
        flowId = (byte) 0xdd;
        handler = new RemoveNodeFromNetworkFlowHandler(transactionKeeper);
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
        transactionKeeper.transitAndSchedule(_,_) >> { args ->
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
        currentState            | callbackStatus                            | newState                  | requestMode
        WAITING_FOR_PROTOCOL    | REMOVE_NODE_STATUS_LEARN_READY            | WAITING_FOR_NODE          | 0
        WAITING_FOR_NODE        | REMOVE_NODE_STATUS_NODE_FOUND             | NODE_FOUND                | 0
        NODE_FOUND              | REMOVE_NODE_STATUS_REMOVING_SLAVE         | SLAVE_FOUND               | 0
        NODE_FOUND              | REMOVE_NODE_STATUS_REMOVING_CONTROLLER    | CONTROLLER_FOUND          | 0
        SLAVE_FOUND             | REMOVE_NODE_STATUS_DONE                   | TERMINATING_REMOVE_NODE   | RemoveNodeFromNeworkMode.REMOVE_NODE_STOP.getCode()
        SLAVE_FOUND             | REMOVE_NODE_STATUS_FAILED                 | CLEANING_UP_ERRORS        | RemoveNodeFromNeworkMode.REMOVE_NODE_STOP.getCode()
        CONTROLLER_FOUND        | REMOVE_NODE_STATUS_DONE                   | TERMINATING_REMOVE_NODE   | RemoveNodeFromNeworkMode.REMOVE_NODE_STOP.getCode()
        CONTROLLER_FOUND        | REMOVE_NODE_STATUS_FAILED                 | CLEANING_UP_ERRORS        | RemoveNodeFromNeworkMode.REMOVE_NODE_STOP.getCode()
        TERMINATING_REMOVE_NODE | REMOVE_NODE_STATUS_DONE                   | TERMINATION_STOP_SENT     | RemoveNodeFromNeworkMode.REMOVE_NODE_STOP.getCode()
        CLEANING_UP_ERRORS      | REMOVE_NODE_STATUS_DONE                   | FAILURE_STOP_SENT         | RemoveNodeFromNeworkMode.REMOVE_NODE_STOP.getCode()
        ABORTING_OPERATION      | REMOVE_NODE_STATUS_DONE                   | CANCELLATION_STOP_SENT    | RemoveNodeFromNeworkMode.REMOVE_NODE_STOP.getCode()
        ABORTING_OPERATION      | REMOVE_NODE_STATUS_NODE_FOUND             | NODE_FOUND                | 0
    }

    def callback(RemoveNodeFromNeworkStatus status) {
        byte[] data = [0x06, 0x06, 0x00, 0x4a, flowId, status.getCode(), 0x00, 0x00];
        return callbackOfData(data);
    }

    def callbackOfData(byte[] data) {
        data[data.length - 1] = FrameUtil.frameCRC(data);
        ViewBuffer buffer = new ViewBuffer(ByteBuffer.wrap(data));
        buffer.setViewRange(0, data.length);
        return new RemoveNodeFromNetworkCallback(buffer);
    }
}
