package com.rposcro.jwavez.serial.model;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

public enum NvmBackupRestoreOperation implements EncodableConstant {

    OPEN(0x00),
    READ(0x01),
    WRITE(0x02),
    CLOSE(0x03)
    ;

    NvmBackupRestoreOperation(int code) {
        EncodableConstantsRegistry.registerConstant(this, (byte) code);
    }
}
