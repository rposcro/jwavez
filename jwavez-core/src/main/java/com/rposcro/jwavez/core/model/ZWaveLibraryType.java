package com.rposcro.jwavez.core.model;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

import java.util.Optional;

public enum ZWaveLibraryType implements EncodableConstant {

    NA_0(0),
    STATIC_CONTROLLER(1),
    CONTROLLER(2),
    ENHANCED_SLAVE(3),
    SLAVE(4),
    INSTALLER(5),
    ROUTING_SLAVE(6),
    BRIDGE_CONTROLLER(7),
    DEVICE_UNDER_TEST(8),
    NA_9(9),
    AV_REMOTE(10),
    AV_DEVICE(11),
    ;

    ZWaveLibraryType(int code) {
        EncodableConstantsRegistry.registerConstant(this, (byte) code);
    }

    public static ZWaveLibraryType ofCode(byte code) {
        return EncodableConstantsRegistry.constantOfCode(ZWaveLibraryType.class, code);
    }

    public static Optional<ZWaveLibraryType> ofCodeOptional(byte code) {
        return EncodableConstantsRegistry.optionalConstantOfCode(ZWaveLibraryType.class, code);
    }
}
