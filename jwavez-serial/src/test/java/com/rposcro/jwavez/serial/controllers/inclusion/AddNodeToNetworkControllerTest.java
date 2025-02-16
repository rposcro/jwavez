package com.rposcro.jwavez.serial.controllers.inclusion;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.classes.BasicDeviceClass;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.classes.GenericDeviceClass;
import com.rposcro.jwavez.core.classes.SpecificDeviceClass;
import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.controllers.builders.AddNodeToNetworkControllerBuilder;
import com.rposcro.jwavez.serial.frames.callbacks.AddNodeToNetworkCallback;
import com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus;
import com.rposcro.jwavez.serial.rxtx.RxTxConfiguration;
import com.rposcro.jwavez.serial.rxtx.RxTxRouterProcess;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.ChecksumUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddNodeToNetworkControllerTest {

    private final static byte CALLED_FLOW_ID = 0x44;

    @Mock
    private RxTxRouterProcess rxTxRouterProcess;

    private AddNodeToNetworkController controller;

    @BeforeEach
    public void setup() throws Exception {
        when(rxTxRouterProcess.getConfiguration()).thenReturn(RxTxConfiguration.builder().build());
        doAnswer(args -> null).when(rxTxRouterProcess).sendRequest(any(SerialRequest.class));

        this.controller = new AddNodeToNetworkControllerBuilder()
            .dongleDevice("/fake")
            .rxTxRouterProcess(rxTxRouterProcess)
            .waitForProgressTimeout(20)
            .waitForTouchTimeout(20)
            .build()
            ;
    }

    @Test
    public void newNodeIsSuccessfullyAdded() throws Exception {
        controller.transactionKeeper.reset();
        controller.flowHandler.startOver(CALLED_FLOW_ID);
        controller.flowStep();
        controller.flowHandler.handleCallback(learnReadyCallback(CALLED_FLOW_ID));
        controller.flowStep();
        controller.flowHandler.handleCallback(nodeFoundCallback(CALLED_FLOW_ID));
        controller.flowStep();
        controller.flowStep();
        controller.flowStep();
        controller.flowHandler.handleCallback(addingSlaveCallback(CALLED_FLOW_ID));
        controller.flowStep();
        controller.flowStep();
        controller.flowHandler.handleCallback(protocolDoneCallback(CALLED_FLOW_ID));
        controller.flowStep();
        controller.flowStep();
        controller.flowStep();
        controller.flowHandler.handleCallback(doneCallback(CALLED_FLOW_ID));
        controller.flowStep();

        NodeInfo node = ((AddNodeToNetworkFlowHandler) controller.flowHandler).getNodeInfo();

        assertNotNull(node);
        assertEquals((byte) 0x88, node.getId().getId());
        assertEquals(BasicDeviceClass.BASIC_TYPE_ROUTING_SLAVE, node.getBasicDeviceClass());
        assertEquals(GenericDeviceClass.GENERIC_TYPE_SENSOR_BINARY, node.getGenericDeviceClass());
        assertEquals(SpecificDeviceClass.SPECIFIC_TYPE_ROUTING_SENSOR_BINARY, node.getSpecificDeviceClass());
        assertEquals(1, node.getCommandClasses().length);
        assertEquals(CommandClass.CMD_CLASS_SENSOR_BINARY, node.getCommandClasses()[0]);
    }

    @Test
    public void nodeNullWhenNewNodeFlowMissed() throws Exception {
        controller.transactionKeeper.reset();
        controller.flowHandler.startOver(CALLED_FLOW_ID);
        controller.flowStep();
        controller.flowHandler.handleCallback(doneCallback(CALLED_FLOW_ID));

        NodeInfo node = ((AddNodeToNetworkFlowHandler) controller.flowHandler).getNodeInfo();
        assertNull(node);
    }

    @Test
    public void transactionFailedWhenTimeoutInProgressOccurs() throws Exception {
        controller.transactionKeeper.reset();
        controller.flowHandler.startOver(CALLED_FLOW_ID);
        controller.flowStep();
        controller.flowHandler.handleCallback(learnReadyCallback(CALLED_FLOW_ID));
        controller.flowStep();
        controller.flowHandler.handleCallback(nodeFoundCallback(CALLED_FLOW_ID));
        controller.flowStep();
        controller.flowHandler.handleCallback(addingSlaveCallback(CALLED_FLOW_ID));
        callFlowForPeriod(35);

        assertTrue(controller.transactionKeeper.isFailed());
    }

    @Test
    public void transactionFailedWhenTimeoutInWaitingForNodeOccurs() throws Exception {
        controller.transactionKeeper.reset();
        controller.flowHandler.startOver(CALLED_FLOW_ID);
        controller.flowStep();
        controller.flowHandler.handleCallback(learnReadyCallback(CALLED_FLOW_ID));
        controller.flowStep();
        controller.flowHandler.handleCallback(nodeFoundCallback(CALLED_FLOW_ID));
        callFlowForPeriod(35);

        assertTrue(controller.transactionKeeper.isFailed());
    }

    private void callFlowForPeriod(long period) throws Exception {
        long timePoint = System.currentTimeMillis() + period;
        while (timePoint > System.currentTimeMillis()) {
            controller.flowStep();
        }
    }

    private AddNodeToNetworkCallback learnReadyCallback(byte flowId) {
        byte[] data = { 0x06, 0x06, 0x00, 0x4a, flowId, AddNodeToNeworkStatus.ADD_NODE_STATUS_LEARN_READY.getCode(), 0x00, 0x00 };
        return callbackOfData(data);
    }

    private AddNodeToNetworkCallback nodeFoundCallback(byte flowId) {
        byte[] data = { 0x06, 0x06, 0x00, 0x4a, flowId, AddNodeToNeworkStatus.ADD_NODE_STATUS_NODE_FOUND.getCode(), 0x00, 0x00 };
        return callbackOfData(data);
    }

    private AddNodeToNetworkCallback addingSlaveCallback(byte flowId) {
        byte[] data = { 0x06, 0x06, 0x00, 0x4a, flowId, AddNodeToNeworkStatus.ADD_NODE_STATUS_ADDING_SLAVE.getCode(),
            (byte) 0x88, 0x04,
            BasicDeviceClass.BASIC_TYPE_ROUTING_SLAVE.getCode(),
            GenericDeviceClass.GENERIC_TYPE_SENSOR_BINARY.getCode(),
            SpecificDeviceClass.SPECIFIC_TYPE_ROUTING_SENSOR_BINARY.getCode(),
            CommandClass.CMD_CLASS_SENSOR_BINARY.getCode(),
            0x00 };
        return callbackOfData(data);
    }

    private AddNodeToNetworkCallback protocolDoneCallback(byte flowId) {
        byte[] data = { 0x06, 0x06, 0x00, 0x4a, flowId, AddNodeToNeworkStatus.ADD_NODE_STATUS_PROTOCOL_DONE.getCode(), 0x00, 0x00 };
        return callbackOfData(data);
    }

    private AddNodeToNetworkCallback doneCallback(byte flowId) {
        byte[] data = { 0x06, 0x06, 0x00, 0x4a, flowId, AddNodeToNeworkStatus.ADD_NODE_STATUS_DONE.getCode(), 0x00, 0x00 };
        return callbackOfData(data);
    }

    private AddNodeToNetworkCallback callbackOfData(byte[] data) {
        data[data.length - 1] = ChecksumUtil.frameCrc(data);
        ImmutableBuffer buffer = ImmutableBuffer.overBuffer(data);
        return new AddNodeToNetworkCallback(buffer);
    }
}
