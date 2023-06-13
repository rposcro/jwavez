package com.rposcro.jwavez.serial.frames.callbacks;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.CallbackFrameModel;
import com.rposcro.jwavez.serial.model.ApplicationUpdateStatus;
import com.rposcro.jwavez.serial.utils.NodeUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@CallbackFrameModel(function = SerialCommand.APPLICATION_UPDATE)
public class ApplicationUpdateCallback extends ZWaveCallback {

    private ApplicationUpdateStatus status;
    private NodeInfo nodeInfo;
    private NodeId nodeId;

    public ApplicationUpdateCallback(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        frameBuffer.position(FRAME_OFFSET_PAYLOAD);
        this.status = ApplicationUpdateStatus.ofCode(frameBuffer.nextByte());

        if (status == ApplicationUpdateStatus.APP_UPDATE_STATUS_SUC_ID) {
            this.nodeId = new NodeId(frameBuffer.nextByte());
            log.debug("Received SUC id update {}", nodeId.getId());
        } else if (status == ApplicationUpdateStatus.APP_UPDATE_STATUS_NODE_INFO_RECEIVED) {
            this.nodeInfo = NodeUtil.decodeNodeInfo(frameBuffer);
            this.nodeId = nodeInfo.getId();
            log.debug("Received info of node {}", nodeInfo.getId());
        } else if (status == ApplicationUpdateStatus.APP_UPDATE_STATUS_NODE_INFO_REQ_FAILED) {
            log.debug("Failed to obtain node info {}", status);
        } else {
            log.info("Unsupported application update status received {}", status);
        }
    }
}
