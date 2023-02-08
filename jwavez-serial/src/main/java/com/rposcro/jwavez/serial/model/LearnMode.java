package com.rposcro.jwavez.serial.model;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

public enum LearnMode implements EncodableConstant {

    LEARN_MODE_DISABLE(0x00),
    LEARN_MODE_CLASSIC(0x01),
    LEARN_MODE_NWI(0x02),
    LEARN_MODE_NWE(0x03),
    ;

    LearnMode(int code) {
        EncodableConstantsRegistry.registerConstant(this, (byte) code);
    }

    public static LearnMode ofCode(byte code) {
        return EncodableConstantsRegistry.constantOfCode(LearnMode.class, code);
    }
}
