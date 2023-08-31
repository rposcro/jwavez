package com.rposcro.jwavez.serial.model;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

public enum TransmitOption implements EncodableConstant {

    TRANSMIT_OPTION_ACK(0x01),
    TRANSMIT_OPTION_LOW_POWER(0x02),
    TRANSMIT_OPTION_AUTO_ROUTE(0x04),
    TRANSMIT_OPTION_NO_ROUTE(0x10),
    TRANSMIT_OPTION_EXPLORE(0x20),
    ;

    TransmitOption(int code) {
        EncodableConstantsRegistry.registerConstant(this, (byte) code);
    }

    public static TransmitOption ofCode(byte code) {
        return EncodableConstantsRegistry.constantOfCode(TransmitOption.class, code);
    }
}
