package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.SerialRequestFactory;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import com.rposcro.jwavez.tools.shell.communication.ApplicationCommandResult;
import com.rposcro.jwavez.tools.shell.communication.SerialCommunicationService;
import com.rposcro.jwavez.tools.utils.SerialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TalkCommunicationService {

    @Autowired
    private SerialCommunicationService serialCommunicationService;

    @Autowired
    private SerialRequestFactory serialRequestFactory;

    public <T extends ZWaveSupportedCommand> T requestTalk(
            int nodeId, ZWaveControlledCommand commandToSend, CommandType expectedResponseType
    ) throws SerialException {
        ZWaveSupportedCommand responseCommand = serialCommunicationService.runApplicationCommandFunction((executor ->
                executor.requestApplicationCommand(
                        new NodeId(nodeId),
                        commandToSend,
                        expectedResponseType,
                        SerialUtils.DEFAULT_TIMEOUT)
        )).getAcquiredSupportedCommand();
        return (T) responseCommand;
    }

    public ApplicationCommandResult<ZWaveSupportedCommand> requestTalk(int nodeId, byte[] payload) throws SerialException {
        ZWaveControlledCommand commandToSend = new ZWaveControlledCommand(payload);
        return serialCommunicationService.runApplicationCommandFunction((executor ->
                executor.requestApplicationCommand(
                        new NodeId(nodeId),
                        commandToSend,
                        SerialUtils.DEFAULT_TIMEOUT)
        ));
    }

    public boolean sendCommand(int nodeId, ZWaveControlledCommand commandToSend) throws SerialException {
        boolean sendResult = serialCommunicationService.runBasicSynchronousFunction((executor) -> {
            SendDataCallback callback = executor.requestCallbackFlow(
                    serialRequestFactory.networkTransportRequestBuilder().createSendDataRequest(
                            new NodeId(nodeId), commandToSend, SerialUtils.nextFlowId()));
            return callback.getTransmitCompletionStatus() == TransmitCompletionStatus.TRANSMIT_COMPLETE_OK;
        });
        return sendResult;
    }
}
