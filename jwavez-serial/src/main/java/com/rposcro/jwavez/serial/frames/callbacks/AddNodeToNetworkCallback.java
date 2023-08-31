package com.rposcro.jwavez.serial.frames.callbacks;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.CallbackFrameModel;
import com.rposcro.jwavez.serial.model.AddNodeToNeworkStatus;
import com.rposcro.jwavez.serial.utils.NodesUtil;

import java.util.Optional;

import lombok.Getter;

@Getter
@CallbackFrameModel(function = SerialCommand.ADD_NODE_TO_NETWORK)
public class AddNodeToNetworkCallback extends FlowCallback {

    private static final int OFFSET_SOURCE_NODE_ID = FRAME_OFFSET_PAYLOAD + 2;
    private static final int OFFSET_NIF_LENGTH = FRAME_OFFSET_PAYLOAD + 3;

    private AddNodeToNeworkStatus status;
    private Optional<NodeInfo> nodeInfo;

    public AddNodeToNetworkCallback(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        this.status = AddNodeToNeworkStatus.ofCode(frameBuffer.nextByte());

        if (isNodeInfoPresent(frameBuffer)) {
            this.nodeInfo = Optional.of(NodesUtil.decodeNodeInfo(frameBuffer));
        } else {
            this.nodeInfo = Optional.empty();
        }
    }

    private boolean isNodeInfoPresent(ImmutableBuffer frameBuffer) {
        return (frameBuffer.getByte(OFFSET_SOURCE_NODE_ID) != 0 && frameBuffer.getByte(OFFSET_NIF_LENGTH) != 0);
    }
}
