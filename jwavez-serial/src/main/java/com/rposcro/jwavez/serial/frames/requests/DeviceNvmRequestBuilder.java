package com.rposcro.jwavez.serial.frames.requests;

import com.rposcro.jwavez.core.buffer.ByteBufferManager;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.model.NvmBackupRestoreOperation;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

import static com.rposcro.jwavez.serial.enums.SerialCommand.NVM_BACKUP_RESTORE;

public class DeviceNvmRequestBuilder extends AbstractRequestBuilder {

    public DeviceNvmRequestBuilder(ByteBufferManager byteBufferManager) {
        super(byteBufferManager);
    }

    public SerialRequest createNvmBackupRestoreOpenRequest() {
        ImmutableBuffer buffer = dataBuilder(NVM_BACKUP_RESTORE, 1)
            .add(NvmBackupRestoreOperation.OPEN.getCode())
            .build();
        return SerialRequest.builder()
            .responseExpected(true)
            .serialCommand(NVM_BACKUP_RESTORE)
            .frameData(buffer)
            .build();
    }

    public SerialRequest createNvmBackupRestoreCloseRequest() {
        ImmutableBuffer buffer = dataBuilder(NVM_BACKUP_RESTORE, 1)
            .add(NvmBackupRestoreOperation.CLOSE.getCode())
            .build();
        return SerialRequest.builder()
            .responseExpected(true)
            .serialCommand(NVM_BACKUP_RESTORE)
            .frameData(buffer)
            .build();
    }

    public SerialRequest createNvmBackupRestoreReadRequest(byte bufferLength, short offset) {
         ImmutableBuffer buffer = dataBuilder(NVM_BACKUP_RESTORE, 4)
            .add(NvmBackupRestoreOperation.READ.getCode())
            .add(bufferLength)
            .addWord(offset)
            .build();
        return SerialRequest.builder()
            .responseExpected(true)
            .serialCommand(NVM_BACKUP_RESTORE)
            .frameData(buffer)
            .build();
    }
}
