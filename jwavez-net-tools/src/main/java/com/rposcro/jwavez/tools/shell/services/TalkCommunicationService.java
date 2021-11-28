package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TalkCommunicationService {

    @Autowired
    private SerialControllerManager serialControllerManager;

    public <T extends ZWaveSupportedCommand> T requestTalk(
            int nodeId, ZWaveControlledCommand commandToSend, CommandType expectedResponseType
    ) throws SerialException {
        NodeId nodeID = new NodeId(nodeId);
        ZWaveSupportedCommand responseCommand = serialControllerManager.runApplicationCommandFunction((executor ->
                executor.requestApplicationCommand(
                        nodeID,
                        commandToSend,
                        expectedResponseType,
                        SerialUtils.DEFAULT_TIMEOUT)
        ));
        return (T) responseCommand;
    }

    public boolean sendCommand(int nodeId, ZWaveControlledCommand commandToSend) throws SerialException {
        NodeId nodeID = new NodeId(nodeId);
        boolean sendResult = serialControllerManager.runBasicSynchronousFunction((executor) -> {
            SendDataCallback callback = executor.requestCallbackFlow(SendDataRequest.createSendDataRequest(
                    nodeID, commandToSend, SerialUtils.nextFlowId()));
            return callback.getTransmitCompletionStatus() == TransmitCompletionStatus.TRANSMIT_COMPLETE_OK;
        });
        return sendResult;
    }
}
