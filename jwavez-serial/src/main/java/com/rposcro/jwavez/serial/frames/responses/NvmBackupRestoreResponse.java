package com.rposcro.jwavez.serial.frames.responses;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import com.rposcro.jwavez.serial.model.NvmBackupRestoreResult;
import lombok.Getter;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

@Getter
@ResponseFrameModel(function = SerialCommand.NVM_BACKUP_RESTORE)
public class NvmBackupRestoreResponse extends ZWaveResponse {

    private NvmBackupRestoreResult result;
    private short bufferLength;
    private int bufferOffset;
    private byte[] buffer;

    public NvmBackupRestoreResponse(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        frameBuffer.position(FRAME_OFFSET_PAYLOAD);
        this.result = NvmBackupRestoreResult.ofCode(frameBuffer.nextByte());
        this.bufferLength = frameBuffer.nextUnsignedByte();
        this.bufferOffset = frameBuffer.nextUnsignedWord();
        if (bufferLength > 0) {
            buffer = frameBuffer.cloneRemainingBytes();
        }
    }
}
