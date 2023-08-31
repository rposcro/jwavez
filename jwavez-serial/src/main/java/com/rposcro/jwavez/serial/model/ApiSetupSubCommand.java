package com.rposcro.jwavez.serial.model;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

public enum ApiSetupSubCommand implements EncodableConstant {

    GET_SUPPORTED_SUBCOMMANDS(0x01),
    SET_TX_STATUS_REPORT(0x02),
    SET_POWER_LEVEL(0x04),
    GET_POWER_LEVEL(0x08),
    GET_MAXIMUM_PAYLOAD_SIZE(0x10),
    GET_LONG_RANGE_MAXIMUM_PAYLOAD_SIZE(0x11),
    GET_RF_REGION(0x20),
    SET_RF_REGION(0x40),
    SET_NODE_ID_BASE_TYPE(0x80)
    ;

    ApiSetupSubCommand(int code) {
        EncodableConstantsRegistry.registerConstant(this, (byte) code);
    }

    public static LearnMode ofCode(byte code) {
        return EncodableConstantsRegistry.constantOfCode(LearnMode.class, code);
    }
}
