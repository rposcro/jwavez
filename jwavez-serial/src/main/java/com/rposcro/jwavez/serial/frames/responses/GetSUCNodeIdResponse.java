package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_SUC_NODE_ID)
public class GetSUCNodeIdResponse extends ZWaveResponse {

    private NodeId sucNodeId;

    public GetSUCNodeIdResponse(ViewBuffer frameBuffer) {
        super(frameBuffer);
        this.sucNodeId = new NodeId(frameBuffer.get(FRAME_OFFSET_PAYLOAD));
    }
}
