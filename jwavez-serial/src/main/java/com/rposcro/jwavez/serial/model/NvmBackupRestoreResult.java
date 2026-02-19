package com.rposcro.jwavez.serial.model;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

public enum NvmBackupRestoreResult implements EncodableConstant {

    OK(0x00),
    ERROR(0x01),
    OPERATION_MISMATCH(0x02),
    OPERATION_DISTURBED(0x02),
    EOF(0xFF)
    ;

    NvmBackupRestoreResult(int code) {
        EncodableConstantsRegistry.registerConstant(this, (byte) code);
    }

    public static NvmBackupRestoreResult ofCode(byte code) {
        return EncodableConstantsRegistry.constantOfCode(NvmBackupRestoreResult.class, code);
    }
}
