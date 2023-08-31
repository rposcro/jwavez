package com.rposcro.jwavez.serial.frames.requests;

import com.rposcro.jwavez.core.buffer.ByteBufferManager;
import com.rposcro.jwavez.core.buffer.ImmutableBufferBuilder;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.model.TransmitOption;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

import static com.rposcro.jwavez.serial.enums.SerialCommand.SEND_DATA;
import static com.rposcro.jwavez.serial.enums.SerialCommand.SEND_DATA_ABORT;

public class NetworkTransportRequestBuilder extends AbstractRequestBuilder {

    public NetworkTransportRequestBuilder(ByteBufferManager byteBufferManager) {
        super(byteBufferManager);
    }

    public SerialRequest createSendDataAbortRequest() {
        return nonPayloadRequest(SEND_DATA_ABORT);
    }

    public SerialRequest createSendDataRequest(NodeId addresseeId, ZWaveControlledCommand controlledCommand, byte callbackFlowId) {
        ImmutableBufferBuilder bufferBuilder = dataBuilder(SEND_DATA, 4 + controlledCommand.getPayloadLength())
                .add(addresseeId.getId())
                .add((byte) controlledCommand.getPayloadLength());

        byte[] commandPayload = controlledCommand.getPayload();
        for (byte bt: commandPayload) {
            bufferBuilder.add(bt);
        }

        bufferBuilder.add((byte) (TransmitOption.TRANSMIT_OPTION_ACK.getCode() | TransmitOption.TRANSMIT_OPTION_AUTO_ROUTE.getCode()));
        bufferBuilder.add(callbackFlowId);

        return SerialRequest.builder()
                .responseExpected(true)
                .serialCommand(SEND_DATA)
                .frameData(bufferBuilder.build())
                .callbackFlowId(callbackFlowId)
                .build();
    }
}
