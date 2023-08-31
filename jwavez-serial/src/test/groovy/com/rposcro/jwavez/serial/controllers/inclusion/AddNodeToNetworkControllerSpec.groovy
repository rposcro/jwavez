package com.rposcro.jwavez.serial.controllers.inclusion

import com.rposcro.jwavez.core.enums.BasicDeviceClass
import com.rposcro.jwavez.core.enums.CommandClass
import com.rposcro.jwavez.core.enums.GenericDeviceClass
import com.rposcro.jwavez.core.enums.SpecificDeviceClass
import com.rposcro.jwavez.serial.buffers.ViewBuffer
import com.rposcro.jwavez.serial.controllers.helpers.CallbackFlowIdDispatcher
import com.rposcro.jwavez.serial.exceptions.FlowException
import com.rposcro.jwavez.serial.frames.callbacks.AddNodeToNetworkCallback
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler
import com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus
import com.rposcro.jwavez.serial.rxtx.RxTxRouterProcess
import com.rposcro.jwavez.serial.utils.ChecksumUtil
import com.rposcro.jwavez.serial.utils.FramesUtil
import spock.lang.Specification

import java.nio.ByteBuffer

class AddNodeToNetworkControllerSpec extends Specification {

    RxTxRouterProcess rxTxRouterProcess;
    AddNodeToNetworkController controller;
    Iterator<Runnable> actions;

    def setup() {
        rxTxRouterProcess = Mock(RxTxRouterProcess.class);
        controller = AddNodeToNetworkController.builder()
                .dongleDevice("/fake")
                .waitForTouchTimeout(20)
                .waitForProgressTimeout(20)
                .build();
        controller.rxTxRouterProcess = rxTxRouterProcess;
    }

    def "new node successfully added"() {
        given:
        rxTxRouterProcess.sendRequest(_) >> {};

        when:
        controller.transactionKeeper.reset();
        controller.flowHandler.startOver(controller.callbackFlowIdDispatcher.nextFlowId());
        controller.flowStep();
        controller.flowHandler.handleCallback(learnReadyCallback(controller.flowHandler.callbackFlowId));
        controller.flowStep();
        controller.flowHandler.handleCallback(nodeFoundCallback(controller.flowHandler.callbackFlowId));
        controller.flowStep();
        controller.flowStep();
        controller.flowStep();
        controller.flowHandler.handleCallback(addingSlaveCallback(controller.flowHandler.callbackFlowId));
        controller.flowStep();
        controller.flowStep();
        controller.flowHandler.handleCallback(protocolDoneCallback(controller.flowHandler.callbackFlowId));
        controller.flowStep();
        controller.flowStep();
        controller.flowStep();
        controller.flowHandler.handleCallback(doneCallback(controller.flowHandler.callbackFlowId));
        controller.flowStep();
        def node = controller.flowHandler.nodeInfo;

        then:
        node != null;
        node.id.getId() == (byte) 0x88;
        node.basicDeviceClass == BasicDeviceClass.BASIC_TYPE_ROUTING_SLAVE;
        node.genericDeviceClass == GenericDeviceClass.GENERIC_TYPE_SENSOR_BINARY;
        node.specificDeviceClass == SpecificDeviceClass.SPECIFIC_TYPE_ROUTING_SENSOR_BINARY;
        node.commandClasses.length == 1;
        node.commandClasses[0] == CommandClass.CMD_CLASS_SENSOR_BINARY;
    }

    def "no node added"() {
        given:
        actions = [
                { controller.flowHandler.handleCallback(learnReadyCallback(controller.flowHandler.callbackFlowId)) },
                {}
        ].iterator();
        rxTxRouterProcess.sendRequest(_) >> { nextCallback() };

        when:
        def node = controller.listenForNodeToAdd();

        then:
        !node.isPresent();
    }

    def "timeout awaiting callback when adding in progress"() {
        given:
        rxTxRouterProcess.sendRequest(_) >> {};

        when:
        controller.transactionKeeper.reset();
        controller.flowHandler.startOver(controller.callbackFlowIdDispatcher.nextFlowId());
        controller.flowStep();
        controller.flowHandler.handleCallback(learnReadyCallback(controller.flowHandler.callbackFlowId));
        controller.flowStep();
        controller.flowHandler.handleCallback(nodeFoundCallback(controller.flowHandler.callbackFlowId));
        controller.flowStep();
        controller.flowHandler.handleCallback(addingSlaveCallback(controller.flowHandler.callbackFlowId));
        executeForPeriod({ controller.flowStep() }, 20);

        then:
        controller.transactionKeeper.failed;
    }

    def "timeout awaiting callback of learn ready from dongle"() {
        when:
        controller.listenForNodeToAdd();

        then:
        FlowException exception = thrown();
        exception.message.startsWith("Process of add node from network transaction failed at state");
    }


    def executeForPeriod(Runnable runnable, long period) {
        long timePoint = System.currentTimeMillis() + period;
        while (timePoint > System.currentTimeMillis()) {
            runnable.run();
        }
    }

    def nextCallback() {
        actions.next().run();
    }

    def learnReadyCallback(funcId) {
        byte[] data = [0x06, 0x06, 0x00, 0x4a, funcId, AddNodeToNeworkStatus.ADD_NODE_STATUS_LEARN_READY.getCode(), 0x00, 0x00];
        return callbackOfData(data);
    }

    def nodeFoundCallback(funcId) {
        byte[] data = [0x06, 0x06, 0x00, 0x4a, funcId, AddNodeToNeworkStatus.ADD_NODE_STATUS_NODE_FOUND.getCode(), 0x00, 0x00];
        return callbackOfData(data);
    }

    def addingSlaveCallback(funcId) {
        byte[] data = [0x06, 0x06, 0x00, 0x4a, funcId, AddNodeToNeworkStatus.ADD_NODE_STATUS_ADDING_SLAVE.getCode(),
                       0x88, 0x04,
                       BasicDeviceClass.BASIC_TYPE_ROUTING_SLAVE.getCode(),
                       GenericDeviceClass.GENERIC_TYPE_SENSOR_BINARY.getCode(),
                       SpecificDeviceClass.SPECIFIC_TYPE_ROUTING_SENSOR_BINARY.getCode(),
                       CommandClass.CMD_CLASS_SENSOR_BINARY.getCode(),
                       0x00];
        return callbackOfData(data);
    }

    def protocolDoneCallback(funcId) {
        byte[] data = [0x06, 0x06, 0x00, 0x4a, funcId, AddNodeToNeworkStatus.ADD_NODE_STATUS_PROTOCOL_DONE.getCode(), 0x00, 0x00];
        return callbackOfData(data);
    }

    def doneCallback(funcId) {
        byte[] data = [0x06, 0x06, 0x00, 0x4a, funcId, AddNodeToNeworkStatus.ADD_NODE_STATUS_DONE.getCode(), 0x00, 0x00];
        return callbackOfData(data);
    }

    def callbackOfData(byte[] data) {
        data[data.length - 1] = ChecksumUtil.frameCrc(data);
        ViewBuffer buffer = new ViewBuffer(ByteBuffer.wrap(data));
        buffer.setViewRange(0, data.length);
        return new AddNodeToNetworkCallback(buffer);
    }
}
