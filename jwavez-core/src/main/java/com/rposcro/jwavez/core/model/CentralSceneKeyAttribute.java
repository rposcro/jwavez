package com.rposcro.jwavez.core.model;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

import java.util.Optional;

public enum CentralSceneKeyAttribute implements EncodableConstant {

    KEY_PRESSED(0),
    KEY_RELEASED(1),
    KEY_HELD_DOWN(2),
    KEY_PRESSES_2_TIMES(3),
    KEY_PRESSES_3_TIMES(4),
    KEY_PRESSES_4_TIMES(5),
    KEY_PRESSES_5_TIMES(6)
    ;

    CentralSceneKeyAttribute(int code) {
        EncodableConstantsRegistry.registerConstant(this, (byte) code);
    }

    public static CentralSceneKeyAttribute ofCode(byte code) {
        return EncodableConstantsRegistry.constantOfCode(CentralSceneKeyAttribute.class, code);
    }

    public static Optional<CentralSceneKeyAttribute> ofCodeOptional(byte code) {
        return EncodableConstantsRegistry.optionalConstantOfCode(CentralSceneKeyAttribute.class, code);
    }
}
