package com.rposcro.jwavez.serial.model;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

public enum RemoveNodeFromNeworkMode implements EncodableConstant {

    REMOVE_NODE_ANY(0x01),
    REMOVE_NODE_CONTROLLER(0x02),
    REMOVE_NODE_SLAVE(0x03),
    REMOVE_NODE_STOP(0x05),
    ;

    RemoveNodeFromNeworkMode(int code) {
        EncodableConstantsRegistry.registerConstant(this, (byte) code);
    }

    public static RemoveNodeFromNeworkMode ofCode(byte code) {
        return EncodableConstantsRegistry.constantOfCode(RemoveNodeFromNeworkMode.class, code);
    }
}
