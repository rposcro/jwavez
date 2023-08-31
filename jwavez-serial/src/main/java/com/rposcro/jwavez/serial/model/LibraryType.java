package com.rposcro.jwavez.serial.model;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

public enum LibraryType implements EncodableConstant {

    LIB_UNKNOWN(0x00),
    LIB_CONTROLLER_STATIC(0x01),
    LIB_CONTROLLER(0x02),
    LIB_SLAVE_ENHANCED(0x03),
    LIB_SLAVE(0x04),
    LIB_INSTALLER(0x05),
    LIB_SLAVE_ROUTING(0x06),
    LIB_CONTROLLER_BRIDGE(0x07),
    LIB_TEST(0x08),
    LIB_AV_REMOTE(0x0a),
    LIB_AV_DEVICE(0x0b);

    private LibraryType(int code) {
        EncodableConstantsRegistry.registerConstant(this, (byte) code);
    }

    public static LibraryType ofCode(byte code) {
        return EncodableConstantsRegistry.constantOfCode(LibraryType.class, code);
    }
}
