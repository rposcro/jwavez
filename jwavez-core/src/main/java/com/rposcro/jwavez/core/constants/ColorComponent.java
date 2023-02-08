package com.rposcro.jwavez.core.constants;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

import java.util.Optional;

public enum ColorComponent implements EncodableConstant {

    WARM_WHITE(0),
    COLD_WHITE(1),
    RED(2),
    GREEN(3),
    BLUE(4),
    AMBER(5),
    CYAN(6),
    PURPLE(7),
    INDEXED(8);

    ColorComponent(int code) {
        EncodableConstantsRegistry.registerConstant(this, (byte) code);
    }

    public static ColorComponent ofCode(byte code) {
        return EncodableConstantsRegistry.constantOfCode(ColorComponent.class, code);
    }

    public static Optional<ColorComponent> ofCodeOptional(byte code) {
        return EncodableConstantsRegistry.optionalConstantOfCode(ColorComponent.class, code);
    }
}
