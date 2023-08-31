package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.model.NetworkStatistics;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_NETWORK_STATS)
public class GetNetworkStatsResponse extends ZWaveResponse {

    private NetworkStatistics networkStatistics;

    public GetNetworkStatsResponse(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        this.networkStatistics = buildNetworkStatistics(frameBuffer);
    }

    public NetworkStatistics buildNetworkStatistics(ImmutableBuffer frameBuffer) {
        frameBuffer.position(FRAME_OFFSET_PAYLOAD);
        return NetworkStatistics.builder()
                .transmittedFramesCount(frameBuffer.nextUnsignedWord())
                .backOffsCount(frameBuffer.nextUnsignedWord())
                .receivedCorrectFramesCount(frameBuffer.nextUnsignedWord())
                .lcrErrorsCount(frameBuffer.nextUnsignedWord())
                .crcErrorsCount(frameBuffer.nextUnsignedWord())
                .foreignHomeIdCount(frameBuffer.nextUnsignedWord())
                .build();
    }
}
