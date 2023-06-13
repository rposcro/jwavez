package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_INIT_DATA)
public class GetInitDataResponse extends ZWaveResponse {

    private static final int NODE_BITMASK_SIZE = 29;

    private byte version;
    private byte capabilities;
    private byte chipType;
    private byte chipVersion;
    private List<NodeId> nodes;

    public GetInitDataResponse(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        frameBuffer.position(FRAME_OFFSET_PAYLOAD);
        this.version = frameBuffer.nextByte();
        this.capabilities = frameBuffer.nextByte();
        this.nodes = parseNodes(frameBuffer);
        this.chipType = frameBuffer.nextByte();
        this.chipVersion = frameBuffer.nextByte();
    }

    private List<NodeId> parseNodes(ImmutableBuffer frameBuffer) {
        int nodesBitMaskSize = frameBuffer.nextByte() & 0xff;
        List<NodeId> nodes = new ArrayList<>(nodesBitMaskSize * 8);

        for (int byteIdx = 0; byteIdx < nodesBitMaskSize; byteIdx++) {
            byte maskByte = frameBuffer.nextByte();
            for (int bitIdx = 0; bitIdx < 8; bitIdx++) {
                byte nodeId = (byte) ((byteIdx * 8) + bitIdx + 1);
                if ((maskByte & (0x01 << bitIdx)) > 0) {
                    nodes.add(new NodeId(nodeId));
                }
            }
        }

        return nodes;
    }

    public List<NodeId> getNodes() {
        return Collections.unmodifiableList(nodes);
    }
}
