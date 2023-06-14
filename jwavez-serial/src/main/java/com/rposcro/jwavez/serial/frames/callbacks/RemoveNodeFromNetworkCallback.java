package com.rposcro.jwavez.serial.frames.callbacks;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.CallbackFrameModel;
import com.rposcro.jwavez.serial.model.RemoveNodeFromNeworkStatus;
import com.rposcro.jwavez.serial.utils.NodesUtil;

import java.util.Optional;

import lombok.Getter;

@Getter
@CallbackFrameModel(function = SerialCommand.REMOVE_NODE_FROM_NETWORK)
public class RemoveNodeFromNetworkCallback extends FlowCallback {

    private static final int OFFSET_SOURCE = FRAME_OFFSET_PAYLOAD + 2;
    private static final int OFFSET_NIF_LENGTH = FRAME_OFFSET_PAYLOAD + 3;

    private RemoveNodeFromNeworkStatus status;
    private Optional<NodeInfo> nodeInfo;

    public RemoveNodeFromNetworkCallback(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        this.status = RemoveNodeFromNeworkStatus.ofCode(frameBuffer.nextByte());
        if (isNodeInfoPresent(frameBuffer)) {
            this.nodeInfo = Optional.of(NodesUtil.decodeNodeInfo(frameBuffer));
        } else {
            this.nodeInfo = Optional.empty();
        }
    }

    private boolean isNodeInfoPresent(ImmutableBuffer frameBuffer) {
        return (frameBuffer.getByte(OFFSET_SOURCE) != 0 && frameBuffer.getByte(OFFSET_NIF_LENGTH) != 0);
    }
}
