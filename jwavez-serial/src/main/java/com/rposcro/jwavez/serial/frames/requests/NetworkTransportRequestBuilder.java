package com.rposcro.jwavez.serial.frames.requests;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.buffers.DisposableFrameBuffer;
import com.rposcro.jwavez.serial.model.TransmitOption;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

import static com.rposcro.jwavez.serial.enums.SerialCommand.SEND_DATA;
import static com.rposcro.jwavez.serial.enums.SerialCommand.SEND_DATA_ABORT;

public class NetworkTransportRequestBuilder extends AbstractRequestBuilder {

    public SerialRequest createSendDataAbortRequest() {
        return nonPayloadRequest(SEND_DATA_ABORT);
    }

    public SerialRequest createSendDataRequest(NodeId addresseeId, ZWaveControlledCommand controlledCommand, byte callbackFlowId) {
        DisposableFrameBuffer buffer = startUpFrameBuffer(FRAME_CONTROL_SIZE + 4 + controlledCommand.getPayloadLength(), SEND_DATA)
                .put(addresseeId.getId())
                .put((byte) controlledCommand.getPayloadLength());

        byte[] commandPayload = controlledCommand.getPayload();
        for (byte bt: commandPayload) {
            buffer.put(bt);
        }

        buffer.put((byte) (TransmitOption.TRANSMIT_OPTION_ACK.getCode() | TransmitOption.TRANSMIT_OPTION_AUTO_ROUTE.getCode()))
                .put(callbackFlowId)
                .putCRC();

        return SerialRequest.builder()
                .responseExpected(true)
                .serialCommand(SEND_DATA)
                .frameData(buffer)
                .callbackFlowId(callbackFlowId)
                .build();
    }
}
